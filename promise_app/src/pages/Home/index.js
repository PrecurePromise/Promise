import React, {useState, useCallback } from 'react';
import {useFocusEffect} from '@react-navigation/native';
import { View, Alert, Image, Text } from 'react-native';
import {myinfo, getMainAlarm, sharingList, sharingAccept, sharingReject, getAlarmDetail} from '../../utils/axios';
import Spinner from 'react-native-loading-spinner-overlay';
import { getMyInfoAction } from '../../modules/user/actions';
import Notifications from '../../utils/Notifications';
import Moment from 'moment';
import { useDispatch } from 'react-redux';
import FirstImg from '../../assets/firstHome.png';
import NoImg from '../../assets/noSchedule.png';
import TodayPromise from '../../components/TodayPromise';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';

const HomePage = ({navigation}) => {
    const [spinVisible, setSpinvisible] = useState();
    const dispatch = useDispatch();
    const [userInfo, setUserInfo] = useState({});
    const [isPre, setIsPre] = useState(false);
    const [myList, setMyList] = useState({});
    const colorList = [['#F4B3B3', '#FFE0E0'], ['#FFDE8A', '#FBEDC9'], ['#BCD27B','#DEEFC9'], ['#B2D3E5', '#D6ECF8'], ['#CCBEF3', '#EAE3FF']];

    const checkSharing = async()=>{
        setSpinvisible(true);
        const shareList = await sharingList();
        if(shareList.length>0){
            shareList.map(item=>{
                Alert.alert(
                    '알람 공유 요청',
                    `${item.userNickname}님의 ${item.alarmTitle} 알람 
공유를 수락하시겠습니까?`,
                    [
                        {
                            text:'예',
                            onPress: ()=>acceptSharing(item.asId)
                        },
                        {
                            text:'아니요',
                            onPress: ()=>rejectSharing(item.asId)
                        }
                    ]
                )
            })
        }
        setSpinvisible(false);
    }

    const acceptSharing = async (id) => {
        const result = await sharingAccept(id);
        if(result===200){
            const alarmdata = await getAlarmDetail(id);
            setNotification(id, alarmdata.alarmTitle, alarmdata.alarmMediList, alarmdata.alarmDayStart, alarmdata.alarmDayEnd, alarmdata.alarmTime1, alarmdata.alarmTime2, alarmdata.alarmTime3);
        }
    }
    
    const setNotification = async(alarmId, title, pillList, myStartDate, myendDate, selectTime1, selectTime2, selectTime3)=>{
        Moment.locale('ko');
        let nowTime = Moment().toDate();
        let cur = Moment(myStartDate).toDate();
        let end = Moment(myendDate).toDate();
        let hour = [];
        let minute = [];
        if(selectTime1){
          hour.push(selectTime1.substring(0,2));
          minute.push(selectTime1.substring(2,4));
        }
        if(selectTime2){
          hour.push(selectTime2.substring(0,2));
          minute.push(selectTime2.substring(2,4));
        }
        if(selectTime3){
          hour.push(selectTime3.substring(0,2));
          minute.push(selectTime3.substring(2,4));
        }
        let listSize = hour.length;
    
        end.setHours(Number(hour[listSize-1]));
        end.setMinutes(Number(minute[listSize-1]));
        end.setSeconds(0);
    
        cur.setSeconds(0);
        let medi = pillList.join(', ');
        let id = 1;
        let registerId = '';
        while (cur<=end){
          for(let idx = 0; idx<listSize; idx++){
            cur.setHours(Number(hour[idx]));
            cur.setMinutes(Number(minute[idx]));
    
            if (cur<nowTime){
              continue;
            }
    
            registerId = `${alarmId}${id}`;
            Notifications.scheduledLocalNotifications(alarmId, registerId, cur, title, medi);
            id ++;
          }
          cur = Moment(cur).add(1, 'd').toDate();
        }
    }

    const rejectSharing = async (id) => {
        await sharingReject(id);
    }

    const getMyInfo = ()=>{
        return myinfo()
        .then(res => {
            if(res.statusCode === 420) navigation.replace('LoginScreen');
            else{
                setUserInfo(res);
                dispatch(getMyInfoAction(res));
            }
        })
    }

    async function getMyAlarm(){
        const result = await getMainAlarm();
        setIsPre(result.preAlarm);
        setMyList(result.alarmList);
    }

    useFocusEffect(
        useCallback(()=>{
            checkSharing();
            getMyInfo();
            getMyAlarm();
            return () => {
                setUserInfo({})
            }
        }, [])
    );    

    const PromiseList = ()=>{
        let result = [];
        if(myList.length>0){
            myList.map((item, index)=>{
                result = result.concat(<TodayPromise data = {item} color={colorList[index%5]}/>);
            })
        }
        return result;
    }

    return (
        <View  style={{ flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor:'white' }}>
            <Spinner visible={spinVisible} />
            {isPre?(
                <>
                    {myList.length>0?(
                        <View style={{justifyContent: 'flex-start', width:'100%', height:'100%', alignItems: 'center'}}>
                            {PromiseList()}
                        </View>
                    ):(
                        <>
                            <Image source={NoImg} resizeMode='contain' style={{width:'40%', height:'40%'}}/>
                            <Text style={{color:'#838385', fontWeight:'bold', fontSize:18}}>오늘은 복약 일정이 없습니다</Text>
                        </>
                    )}
                </>
            ):(
                <>
                    <Image source={FirstImg} resizeMode='contain' style={{width:'40%', height:'40%'}}/>
                    <Text style={{color:'black', fontWeight:'bold', fontSize:18}}>복약 일정을 등록해주세요</Text>
                    <View style={{flexDirection: "row", marginTop:5, alignItems: 'center'}}>
                        <Icon name='calendar-blank' color='#838385' size={16}/>
                        <Text style={{color:'#838385', fontSize:16, marginLeft:5}}>버튼을 누르시면 등록할 수 있습니다</Text>
                    </View>
                </>
            )}
        </View>
    );
};
export default HomePage;