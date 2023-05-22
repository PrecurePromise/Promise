import React, {useState, useEffect} from 'react';
import { View, ScrollView, Text, Modal, TouchableOpacity, TextInput} from 'react-native';
import Spinner from 'react-native-loading-spinner-overlay';
import Icon from 'react-native-vector-icons/AntDesign';
import TimeSelect from '../../components/TimeSelect';
import DateSelect from '../../components/DateSelect';
import FindUser from '../../components/FindUser';
import Toggle from '../../components/Toggle';
import AlarmList from '../../components/atoms/AlarmList';
import AddPill from '../../components/AddPill';
import ShareUser from '../../components/ShareUser';
import OCRModal from '../../components/OCRModal';
import DirectModal from '../../components/DirectModal';
import Moment from 'moment';
import "moment/locale/ko";
import PillModal from '../../components/PillModal';
import Notifications from '../../utils/Notifications';
import {enrollAlarm} from '../../utils/axios';


const AlarmAdd = ({navigation}) => {
  const [spinVisible, setSpinvisible] = useState();
  const [title, onChangeTitle] = useState('');
  const [isOn, setIsOn] = useState(false);
  const [pillList, setPillList] = useState([]);
  const [userList, setUserList] = useState([]);
  const [ocrPillData, setOcrPillData] = useState([]);
  const [tag, setTag] = useState('');
  const [isChange, setIsChange] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [ocrModal, setOcrModal] = useState(false);
  const [addModal, setAddModal] = useState(false);
  const [myModal, setMyModal] = useState(false);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [selectTime1, setSelectTime1] = useState(null);
  const [selectTime2, setSelectTime2] = useState(null);
  const [selectTime3, setSelectTime3] = useState(null);
  const [mediId, setMediId] = useState(1);

  const addList = data => {
    if (data.mediName && data.mediSerialNum) {
      let flag = false;
      for(let i=0;i<pillList.length;i++){
        if(pillList[i].id===data.mediSerialNum){
          flag = true;
          break;
        }
      }
      if(flag === false) setPillList([...pillList, {id: data.mediSerialNum, name: data.mediName}]);
      else alert('이미 추가한 약입니다.');
    }else if(data.mediName){
      setPillList([...pillList, {id: mediId, name: data.mediName}]);
      setMediId(mediId+1);
    }
    setIsChange(true);
  };

  const addUser = selectUser => {
    if (selectUser.userEmail) {
      let flag = false;
      for (let i = 0; i < userList.length; i++) {
        if (userList[i].id == selectUser.userEmail) {
          flag = true;
          break;
        }
      }
      if (flag === false) {
        setUserList([
          ...userList,
          {id: selectUser.userEmail, name: selectUser.userNickname},
        ]);
      } else {
        alert('이미 추가한 사용자입니다.');
      }
    } else {
      alert('선택한 사용자가 없습니다.');
    }
    setModalVisible(false);
    setIsChange(true);
  };

  const removeList = id => {
    setPillList(pillList.filter(pill => pill.id !== id));
    setIsChange(true);
  };

  useEffect(() => {
    setIsChange(false);
  }, [isChange]);

  const myPillList = () => {
    let result = [];
    if (pillList.length>0) {
      pillList.map(item => {
        result = result.concat(
          <AlarmList item={item} remove={data => removeList(data)} />,
        );
      });
    }
    return result;
  };

  const myUserList = () => {
    let result = [];
    if (userList.length>0) {
      userList.map(item => {
        result = result.concat(
          <AlarmList item={item} remove={data => removeUserList(data)} />,
        );
      });
    }
    return result;
  };

  const removeUserList = id => {
    setUserList(userList.filter(user => user.id !== id));
    setIsChange(true);
  };

  const addOCRList = data => {
    if (data.length>0) {
      setPillList(pillList.concat(data));
    }
    setIsChange(true);
  };

  function myMediList() {
    let result = [];
    if (pillList.length>0) {
      pillList.map(item => {
        result = result.concat(item.name);
      });
    }
    return result;
  }

  function myShareList() {
    let result = [];
    if (userList.length>0) {
      userList.map(item => {
        result = result.concat(item.id);
      });
    }
    return result;
  }

  function myTagList() {
    let result = [];
    if (tag.length>0) {
      result = tag.split('#');
    }
    return result;
  }

  function myStartDate() {
    if (startDate) {
      return startDate;
    } else {
      return Moment().format('YYYY-MM-DD');
    }
  }

  function myendDate() {
    if (endDate) {
      return endDate;
    } else {
      return Moment().format('YYYY-MM-DD');
    }
  }

  const addalarm = async () => {
    let alarmYN = 0;
    if (isOn === true) {
      alarmYN = 1;
    }
    if(title.length>0 && (Moment(myendDate()).isSame(Moment(myStartDate()))||Moment(myendDate()).isAfter(Moment(myStartDate()))) && myMediList().length>0 && (alarmYN===0||(alarmYN===1 && (selectTime1 || selectTime2 || selectTime3)))){
      setSpinvisible(true);
      const result = await enrollAlarm( title, alarmYN, selectTime1, selectTime2, selectTime3, myStartDate(), myendDate(), myMediList(), myTagList(), myShareList());
      if(alarmYN===1) setNotification(result);
      setSpinvisible(false);
      navigation.goBack();
    }else if(title.length===0){
      alert('복용명을 입력해주세요.');
    }else if(Moment(myendDate()).isBefore(Moment(myStartDate()))){
      alert('종료일이 시작일보다 빠를 수 없습니다.');
    }else if(myMediList().length===0){
      alert('약 정보를 입력해주세요.');
    }else if(alarmYN===1 && (selectTime1===null && selectTime2===null && selectTime3===null)){
      alert('알람 허용시 최소 1개의 알람을 등록해주세요.');
    }
  };

  const setNotification = async(alarmId)=>{
    Moment.locale('ko');
    let nowTime = Moment().toDate();
    let cur = Moment(myStartDate()).toDate();
    let end = Moment(myendDate()).toDate();
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
    let medi = myMediList().join(', ');
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

  return (
    <View style={{flex: 1, alignItems: 'center', backgroundColor: 'white'}}>
      <Spinner visible={spinVisible} />
      <View style={{width: '90%', alignItems: 'flex-start', marginTop: 10}}>
        <Icon.Button
          name="left"
          color="black"
          backgroundColor="white"
          size={25}
          onPress={() => navigation.goBack()}
        />
      </View>
      <ScrollView
        style={{width: '100%', margin: 10}}
        contentContainerStyle={{alignItems: 'center', margin: 10}}>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            width: '90%',
            justifyContent: 'center',
            height: 50
          }}>
          <Text
            style={{
              fontSize: 15,
              color: 'black',
              fontWeight: 'bold',
              width: '20%'
            }}>
            복용명
          </Text>
          <View
            style={{
              width: '78%',
              backgroundColor: '#E9E9E9',
              height: 40,
              borderRadius: 20,
              alignItems: 'center',
              justifyContent: 'center'
            }}>
            <TextInput
              onChangeText={onChangeTitle}
              value={title}
              style={{
                width: '80%',
                color: 'black',
                backgroundColor: '#E9E9E9',
                borderRadius: 20,
                textAlign: 'center'
              }}
            />
          </View>
        </View>
        <DateSelect
          selectedStart={data => setStartDate(data)}
          selectedEnd={data => setEndDate(data)}
        />
        <AddPill
          add={data => setAddModal(data)}
          ocradd={data => setOcrModal(data)}
          ocrdata={data => setOcrPillData(data)}
        />
        <Modal animationType={'fade'} transparent={true} visible={addModal}>
          <PillModal
            visible={data => setAddModal(data)}
            selected={data => addList(data)}
            my = {data=>setMyModal(data)}
          />
        </Modal>
        <Modal animationType={'fade'} transparent={true} visible={ocrModal}>
          <OCRModal
            data={ocrPillData}
            selected={data => addOCRList(data)}
            visible={data => setOcrModal(data)}
          />
        </Modal>
        <Modal animationType={'fade'} transparent={true} visible={myModal}>
          <DirectModal
            visible={data => setMyModal(data)}
            selected={data => addList(data)}
          />
        </Modal>
        {myPillList()}
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            width: '90%',
            justifyContent: 'center',
            height: 50
          }}>
          <Text
            style={{
              fontSize: 15,
              color: 'black',
              fontWeight: 'bold',
              width: '20%'
            }}>
            태그
          </Text>
          <View
            style={{
              width: '78%',
              backgroundColor: '#E9E9E9',
              height: 40,
              borderRadius: 20,
              alignItems: 'center',
              justifyContent: 'center'
            }}>
            <TextInput
              placeholder="나만의 태그를 #태그로 입력해주세요."
              placeholderTextColor = "#626262"
              onChangeText={setTag}
              value={tag}
              style={{
                width: '80%',
                color: 'black',
                backgroundColor: '#E9E9E9',
                borderRadius: 20,
                textAlign: 'center'
              }}
            />
          </View>
        </View>
        <Toggle result={data => setIsOn(data)} />
        {isOn ? (
          <View>
            <TimeSelect selected={data => setSelectTime1(data)} data="1" />
            <TimeSelect selected={data => setSelectTime2(data)} data="2" />
            <TimeSelect selected={data => setSelectTime3(data)} data="3" />
            <ShareUser result={data => setModalVisible(data)} />
            {myUserList()}
            <Modal
              animationType={'fade'}
              transparent={true}
              visible={modalVisible}>
              <FindUser
                selected={data => addUser(data)}
                visible={data => setModalVisible(data)}
              />
            </Modal>
          </View>
        ) : null}
        <View style={{width: '90%', margin: 10}}>
          <TouchableOpacity
            style={{
              backgroundColor: '#A3BED7',
              color: 'black',
              alignItems: 'center',
              borderRadius: 12,
              height: 50,
              justifyContent: 'center'
            }}
            onPress={() => addalarm()}>
            <Text style={{color: 'black', fontSize: 20, fontWeight: 'bold'}}>등록하기
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </View>
  );
};
export default AlarmAdd;
