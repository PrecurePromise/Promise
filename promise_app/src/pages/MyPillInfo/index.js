import React, { useEffect, useState } from 'react';
import { View, ScrollView, Text, StyleSheet } from 'react-native';
import InfoText from '../../components/atoms/InfoText';
import InfoWarinings from '../../components/InfoWarinings';
import PillInfo from '../../components/PillInfo';
import { getMediDetailAPI } from '../../utils/axios';
import Icon from 'react-native-vector-icons/AntDesign';
import Spinner from 'react-native-loading-spinner-overlay';

const MyPillInfo = (props) => {
    const [mediDetail, setMediDetail] = useState([]);
    const [spinVisible, setSpinvisible] = useState();

    const getMediDetail = async () => {
        setSpinvisible(true);
        const res = await getMediDetailAPI(props.route.params.serialNumber);
        setMediDetail(res);
        setSpinvisible(false);
    }

    useEffect(() => {
        getMediDetail();
    }, []);

    return (
        <View style={{ flex: 1, backgroundColor: '#F9F9F9' }}>
            <Spinner visible={spinVisible} />
            <View style={{width: '90%', alignItems: 'flex-start', marginTop: 10}}>
                <Icon.Button
                name="left"
                color="black"
                backgroundColor="#F9F9F9"
                size={25}
                onPress={() => props.navigation.goBack()}
                />
            </View>
            <View style={style.pillInfoCard}>
                <PillInfo
                    name={mediDetail.mediName}
                    company={mediDetail.mediCompany}
                    imgUrl={mediDetail.mediImgUrl}
                />
                <InfoWarinings
                    ageCare={mediDetail.mediAgeCare}
                    pregnancyCare={mediDetail.mediPregnancyCare}
                    elderlyCare={mediDetail.mediElderlyCare}
                />
            </View>
                {mediDetail.mediIngredient == '' && 
                mediDetail.mediEfficacy == '' &&
                mediDetail.mediTakeWay == '' &&
                mediDetail.mediStoreWay == '' &&
                mediDetail.mediPrecautionsBefore == '' &&
                mediDetail.mediPrecautionsAfter == '' &&
                mediDetail.mediNotWith == '' &&
                mediDetail.mediAllergy == ''
                ? (
                    <View style={{ width: '100%', height:'40%', alignItems: 'center', justifyContent: 'center' }}>
                        <Text style={{ fontSize: 20, color: '#BBBBBB' }}>이 약에 대한 상세 정보가 없습니다.</Text>
                        <Text style={{fontSize:20, color:'#BBBBBB'}}>의사 또는 약사와 상의해주세요.</Text>
                    </View>
                    ) : (
                        <ScrollView style={style.pillDetailInfo} contentContainerStyle={{marginTop: 10, marginBottom: 10}}>
                            {mediDetail.mediIngredient ? (
                                <InfoText title='성분' desc={mediDetail.mediIngredient}/>
                            ): (null)}
                            {mediDetail.mediEfficacy ? (
                                <InfoText title='효능' desc={mediDetail.mediEfficacy}/>
                            ): (null)}
                            {mediDetail.mediTakeWay ? (
                                <InfoText title='복용 방법' desc={mediDetail.mediTakeWay}/>
                            ): (null)}
                            {mediDetail.mediStoreWay ? (
                                <InfoText title='보관 방법' desc={mediDetail.mediStoreWay} />
                            ): (null)}
                            {mediDetail.mediPrecautionsBefore ? (
                                <InfoText title='복용 전 주의 사항' desc={mediDetail.mediPrecautionsBefore} />
                            ): (null)}
                            {mediDetail.mediPrecautionsAfter ? (
                                <InfoText title='복용 후 주의 사항' desc={mediDetail.mediPrecautionsAfter} />
                            ): (null)}
                            {mediDetail.mediNotWith ? (
                                <InfoText title='함께 먹지 말아야 하는 것들' desc={mediDetail.mediNotWith} />
                            ): (null)}
                            {mediDetail.mediAllergy ? (
                                <InfoText title='발생가능성 있는 증상들' desc={mediDetail.mediAllergy}/>
                            ) : (null)}
                        </ScrollView>
                    )
                }
        </View>
    );
};
const style = StyleSheet.create({
    pillDetailInfo: {
        // 노말하게 띄우기
        margin: 10,
        marginTop: 0,
        // 카드형
        borderWidth: 0.3,
        borderColor: '#BBBBBB',
        borderRadius: 5,
        backgroundColor: 'white'
    },
    pillInfoCard: {
        margin: 0,
        marginBottom: 0,
    }
})
export default MyPillInfo;

