import React, {useState, useCallback} from 'react';
import {useFocusEffect} from '@react-navigation/native';
import {View, Image, Modal, Platform} from 'react-native';
import Logo from '../../assets/Promise_Logo.png';
import GoogleLoginBtn from '../../components/GoogleLoginBtn';
import AppleLoginBtn from '../../components/AppleLoginBtn';
import SignInModal from '../../components/SignInModal';
import LoginBtn from '../../components/atoms/LoginBtn';
import LoginModal from '../../components/LoginModal';
import SocialModal from '../../components/SocialModal';
import {userAPI} from '../../utils/axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Spinner from 'react-native-loading-spinner-overlay';

const Login = (props) => {
  const [userModal, setUserModal] = useState(false);
  const [socialModal, setSocialModal] = useState(false);
  const [loginModal, setLoginModal] = useState(false);
  const [id, setId] = useState('');
  const [pw, setPw] = useState('');
  const [profile, setProfile] = useState(null);
  const [type, setType] = useState();
  const [spinVisible, setSpinvisible] = useState();

  async function checkLogin(){
    if(await AsyncStorage.getItem('refreshToken')!==null){
      props.navigation.replace('appscreen');
    }
  }
  useFocusEffect(
    useCallback(()=>{
      checkLogin();
    },[])
  );

  const SocialSignin = async (data) => {
    setSpinvisible(true);
    if (data.email){
      setId(data.email);
      if(data.profile){ 
        setProfile(data.profile);
      }
      setType(data.type);
      const res = await userAPI.social(data.email, pw, data.type);
      setSpinvisible(false);
      if (res === 404) {
        setSocialModal(true);
      }else if(res===402){
        alert('Google 계정으로 가입된 계정입니다. Google로 계속하기를 시도해주세요.');
      }else if(res===403){
        alert('Apple 계정으로 가입된 계정입니다. Apple로 계속하기를 시도해주세요.');
      }else if(res===405){
        alert('일반 계정으로 가입된 계정입니다. 일반 로그인을 시도해주세요.');
      }else{
        props.navigation.replace('appscreen');
      }
    }
    setSpinvisible(false);
  };

  const NomalLogin = async (data) =>{
    const res = await userAPI.login(data.id, data.pw, 0);

    if(res===404){
      alert('존재하지 않는 계정입니다.');
    }else if(res===402){
      alert('Google 계정으로 가입된 계정입니다. Google로 계속하기를 시도해주세요.');      
    }else if(res===403){
      alert('Apple 계정으로 가입된 계정입니다. Apple로 계속하기를 시도해주세요.');      
    }else if(res===401){
      alert('잘못된 비밀번호입니다.');      
    }else {
      props.navigation.replace('appscreen');
    }
  };

  const normalData = async(data) =>{
    try{
      setSpinvisible(true);
      await userAPI.join(data.id, data.pw, data.nick, data.profile, data.petName, 0);
      await userAPI.login(data.id, data.pw, 0)
      .then((res) =>{
        setSpinvisible(false);
        props.navigation.replace('appscreen');
      });
      setSpinvisible(false);
    }catch(e){
      setSpinvisible(false);
    }
  };

  const socialData = async(data) =>{
    try{
      setSpinvisible(true);
      await userAPI.join(id, pw, data.nick, profile, data.petName, type);
      await userAPI.social(id, pw, type)
      .then((res) =>{
        setSpinvisible(false);
        props.navigation.replace('appscreen');
      });
      setSpinvisible(false);
    }catch(e){
      setSpinvisible(false);
    }
  };

  return (
    <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
      <Spinner visible={spinVisible} />
      <View style={{justifyContent: 'center'}}>
        <Image source={Logo} style={{height: '60%'}} resizeMode='contain'/>
      </View>
      {Platform.OS==='android'?(
        <View style={{alignItems: 'center'}}>
          <GoogleLoginBtn data={(data)=>SocialSignin(data)}/>
          <View style={{flexDirection: 'row', justifyContent: 'space-between', width: 290}}>
            <LoginBtn title='일반 로그인' func={()=>setLoginModal(true)}/>
            <LoginBtn title='일반 회원가입' func={()=>setUserModal(true)}/>
          </View>
        </View>
      ):(
        <View style={{alignItems: 'center'}}>
          <GoogleLoginBtn data={(data)=>SocialSignin(data)}/>
          <AppleLoginBtn data={(data) => SocialSignin(data)} />
          <View style={{flexDirection: 'row', justifyContent: 'space-between', width: 300}}>
            <LoginBtn title='로그인' func={()=>setLoginModal(true)}/>
            <LoginBtn title='이메일 회원가입' func={()=>setUserModal(true)}/>
          </View> 
        </View>
      )}
      <Modal animationType={'fade'} transparent={true} visible={loginModal}>
        <LoginModal user={(data)=>NomalLogin(data)} next={(data)=>setLoginModal(data)} exit={(data)=>setLoginModal(data)}/>
      </Modal>
      <Modal animationType={'fade'} transparent={true} visible={userModal}>
        <SignInModal user={(data)=>normalData(data)} now={(data)=>setUserModal(data)} exit={(data)=>setUserModal(data)}/>
      </Modal>
      <Modal animationType={'fade'} transparent={true} visible={socialModal}>
        <SocialModal user={(data)=>socialData(data)} now={(data)=>setSocialModal(data)} exit={(data)=>setSocialModal(data)}/>
      </Modal>
    </View>
  )
}

export default Login;