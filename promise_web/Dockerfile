# 이미지 사용(node)
FROM node:14 as build-stage

# 명령어가 실행될 WORKDIR 설정
WORKDIR /app

# 패키지 파일 WORKDIR로 복사
COPY package*.json ./

# 의존성 전체 설치(실서비스 배포 시, --production 옵션 추가)
RUN npm install

# 모든 파일들을 WORKDIR로 복사
COPY . .

# Browserslist update
RUN npx browserslist@latest --update-db

# 프로젝트 빌드
RUN npm run build

# 이미지 사용(nginx)
FROM nginx:stable-alpine as production-stage

# app 디렉토리 생성
RUN mkdir /app

# nginx의 default.conf를 삭제
RUN rm /etc/nginx/conf.d/default.conf

# HOST PC의 nginx.conf를 우측의 경로로 복사
COPY ./nginx.conf /etc/nginx/conf.d

# 빌드한 바이너리를 실행할 이미지로 전달
COPY --from=build-stage ./app/dist /app

# 도커 컨테이너 외부에 노출할 포트 지정
EXPOSE 80

# 컨테이너 실행 시 자동으로 실행할 명령어
CMD ["nginx", "-g", "daemon off;"]
