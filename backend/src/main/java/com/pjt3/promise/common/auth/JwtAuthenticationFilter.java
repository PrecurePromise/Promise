package com.pjt3.promise.common.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjt3.promise.exception.CustomException;
import com.pjt3.promise.exception.ErrorCode;
import com.pjt3.promise.exception.ErrorResponse;
import com.pjt3.promise.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pjt3.promise.common.util.JwtTokenUtil;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.service.UserService;

// 요청헤더에 jwt 토큰이 있는 경우, 토큰 검증 및 인증 처리 로직 정의
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final UserRepository userRepository;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
				throws ServletException, IOException	{
		
		// request header에 Authorization(key) : Bearer Token(Value) 형태로 날아옴
		String header = request.getHeader(JwtTokenUtil.HEADER_STRING);
		
		// 헤더가 Bearer로 시작하지 않거나 null인 경우 filter 적용
		if(header == null || !header.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			// authorization 수행
			Authentication authentication = getAuthentication(request);
			// jwt 토큰으로부터 획득한 인증 정보(authentication) 설정
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (TokenExpiredException ex) {
			ErrorResponse errorResponse = ErrorResponse.builder()
					.statusCode(ErrorCode.EXPIRED_AUTH_TOKEN.getStatusCode())
					.message(ErrorCode.EXPIRED_AUTH_TOKEN.getMessage())
					.code(ErrorCode.EXPIRED_AUTH_TOKEN.getCode())
					.build();
			
			response.setStatus(ErrorCode.EXPIRED_AUTH_TOKEN.getStatusCode());
			response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
			response.getWriter().flush();

			return;
		} catch (Exception ex) {
			filterChain.doFilter(request, response);
			return;
		}
		
		filterChain.doFilter(request, response);
		
	}
	
	@Transactional(readOnly = true)
	public Authentication getAuthentication(HttpServletRequest request) throws Exception {
		// String token = Bearer token(header.payload.signature)
		// 요청 헤더에 Authorization 키값에 jwt 토큰이 포함된 경우에만, 토큰 검증 및 인증 처리 로직 실행.
		String token = request.getHeader(JwtTokenUtil.HEADER_STRING);
		
		if (token != null) {
			// token을 parse, validate 수행
			JWTVerifier verifier = JwtTokenUtil.getVerifier();
			try {
				JwtTokenUtil.handleError(verifier, token);
			} catch (TokenExpiredException ex) {
				throw ex;
			}
			// token decoding
			System.out.println("*** 토큰 디코딩 ***");
			DecodedJWT decodedJWT = verifier.verify(token.replace(JwtTokenUtil.TOKEN_PREFIX, ""));
			System.out.println("decodedJWT : " + decodedJWT);
			String userEmail = decodedJWT.getSubject();
			System.out.println("userEmail : " + userEmail);
			
			
			// JWT 토큰에서 얻은 유저이메일로 DB에서 그 유저가 있는지 확인
			if(userEmail != null) {
				System.out.println("이메일은 있다 ");
				User user = userRepository.findUserByUserEmail(userEmail);
				if(user != null) {
					// 식별된 정상 유저인 경우, 요청 context 내에서 참조가능한 인증정보(jwtAuthentication) 생성
					PMUserDetails pmUserDetails = new PMUserDetails(user);
					UsernamePasswordAuthenticationToken jwtAuthentication = new UsernamePasswordAuthenticationToken(userEmail, null, pmUserDetails.getAuthorities());
					jwtAuthentication.setDetails(pmUserDetails);
					return jwtAuthentication;
				}
			}
			return null;
		}
		return null;
	}
	
}
