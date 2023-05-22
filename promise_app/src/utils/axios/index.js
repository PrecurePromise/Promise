import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

let request = axios.create({
  // baseURL: 'http://localhost:8080/api',
  baseURL: 'https://promise-precure.site/api',
});

request.interceptors.request.use(
  async (config)=>{
    if (await AsyncStorage.getItem('accessToken')) {
      config.headers['Authorization'] = await AsyncStorage.getItem('accessToken');
    } else {
      config.headers['Authorization'] = '';
    }
    return config;
  }
);

// interceptor API 다중 요청 처리 로직
let isTokenRefreshing = false;
let refreshSubscribers = [];

const onTokenRefreshed = (accessToken) => {
  refreshSubscribers.map((callback) => callback(accessToken));
};

const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};

request.interceptors.response.use(
  (response) => {
    return response;
  },
  async(err)=>{
    const originalConfig = err.config;

    if(err.response.status === 420){
      console.log("에러 statusCode : " + err.response.status);
      console.log("error response data : \n" + JSON.stringify(err.response.data));
      // isTokenRefreshing이 false 인 경우만 token reissue 요청
      if (!isTokenRefreshing) {
        isTokenRefreshing = true;
        
        const curRefreshToken = await AsyncStorage.getItem('refreshToken');

        AsyncStorage.removeItem('refreshToken');
        AsyncStorage.removeItem('accessToken');

        const data = await request.post('/auth/reissue',{
                          refreshToken : curRefreshToken
                        }).then((response) => {
                          response.data

                          AsyncStorage.setItem('refreshToken', response.data.newRefreshToken);
                          setToken(response.data.newAccessToken);

                        }).catch((err) => {
                          err.response.data
                        });
                                    
        const newAccessToken = await AsyncStorage.getItem('accessToken');

        isTokenRefreshing = false;
        request.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;

        // 새로운 토큰으로 지연되었던 요청 진행
        onTokenRefreshed(newAccessToken);
        
        return request(originalConfig);
      }
      // 토큰이 재발급 되는 동안의 요청은 refreshSubscribers에 저장
      const retryOriginalRequest = new Promise((resolve) => {
        addRefreshSubscriber((accessToken) => {
          originalConfig.headers.Authorization = 'Bearer ' + accessToken;
          resolve(request(originalConfig));
        });
      });
      return retryOriginalRequest;
    }
    return Promise.reject(err);
  }
);

function setToken(value) {
  AsyncStorage.setItem('accessToken', `Bearer ${value}`);
}

export const myinfo = async () => {
  return await request.get(`/users`, {
  })
  .then(response => {
    return response.data;
  }).catch(err => {
    return err.response.data;
  });
};

export const withdraw = async()=>{
  return await request.delete(`/users`, {
  })
  .then(response => {
    return response.data.statusCode;
  }).catch(err => {
    return err.response.data;
  });
}

export const shareUser = async searchKeyword => {
  return await request
    .get('/users/sharing', {
      params: {
        searchKeyword: searchKeyword,
      },
    })
    .then(response => {
      return response.data;
    }).catch(err => {
      return err.response.data;
    });
};

export const getAlarmDetail = async (alarmId) => {
  return await request.get(`/alarms/detail/${alarmId}`, {
      params: {
        alarmId: alarmId,
      },
    })
    .then(response => {
      return response.data;
    }).catch(err => {
      return err.response.data;
    });
};

export const deleteAlarm = async(alarmId)=>{
  return await request.delete(`/alarms/${alarmId}`,{
    params: {
      alarmId: alarmId
    }
  }).then(response => {
    return response.data;
  }).catch(err => {
    return err.response.data;
  })
}

export const getMainAlarm = async () => {
  return await request.get(`/alarms/main`, {
    })
    .then(response => {
      return response.data;
    });
};

export const getVisual = async () => {
  return await request.get(`/visual`, {
    })
    .then(response => {
      return response.data.UsersTagList;
    });
};

export const getPeriod = async (pageNum) => {
  return await request.get(`/alarms/${pageNum}`, {
    })
    .then(response => {
      return response.data;
    }).catch(err => {
      return err.response.data;
    });
};

export const getAlarmlist = async (nowDate)=>{
  return await request.get('/alarms', {
      params: {
        nowDate: nowDate,
      },
    })
    .then(response => {
      return response.data.alarmList;
    })
    .catch(err => {
      return err.response.data;
    });
}

export const modifyAlarm = async(alarmId, alarmTitle, alarmYN, alarmTime1, alarmTime2, alarmTime3, alarmDayStart, alarmDayEnd, alarmMediList, tagList)=>{
  return await request.put('/alarms', {
    alarmId, alarmTitle, alarmYN, alarmTime1, alarmTime2, alarmTime3, alarmDayStart, alarmDayEnd, alarmMediList, tagList
  })
  .then(response => {
    return response.data;
  })
  .catch(err => {
    return err.response.data;
  });
}

export const sharingList = async()=>{
  return await request.get('/sharings',{
  }).then((response) => {
    return response.data.alarmShareList;
  }).catch((err) => {
    return err.response;
  })
}

export const sharingAccept = async(alarmId)=>{
  return await request.post('/sharings/accept',{
      asId: alarmId
  }).then((response) => {
    return response.data.statusCode;
  }).catch((err) => {
    return err.response;
  })
};

export const sharingReject = async(alarmId)=>{
  return await request.delete('/sharings/reject',{
    params:{
      alarmId: alarmId
    }
  }).then((response) => {
    return response.data.statusCode;
  }).catch((err) => {
    return err.response;
  })
};

export const getCalendar = async(nowMonth)=>{
  return await request.get('/alarms/calendar', {
    params: {
      nowMonth: nowMonth,
    },
  })
  .then(response => {
    return response.data.alarmList;
  })
  .catch(err => {
    return err.response.data;
  });
}

export const ocrList = async text => {
  return await request.post('/alarms/ocr',{
      text,
    })
    .then(response => {
      return response.data.mediList;
    })
    .catch(err => {
      return err.response.data;
    });
};

export const uploadProfile = async (userProfileUrl) => {
  return await request.put('/users/profile',{
        userProfileUrl,
      }
    )
    .then(response => {
      return response.data.statusCode;
    })
    .catch(err => {
      return err.response.data;
    });
};

export const searchMedicine = async searchKeyword => {
  return await request
    .get('/medicines/alarm', {
      params: {
        searchKeyword: searchKeyword,
      },
    })
    .then(response => {
      return response.data;
    })
    .catch(err => {
      return err.response.data;
    });
};

export const modifyNick = async (userNickname)=>{
  return await request.get(`/users/me/nickname/${userNickname}`,{
  }).then((response)=>{
    return response.data;
  }).catch(err => {
    return err.response.data;
  });
}

export const changeInfo = async(userNickname, petName)=>{
  return await request.put('/users',{
    userNickname, petName
  })
  .then(response => {
    return response.data.statusCode;
  })
  .catch(err => {
    return err.response.data;
  });
}

export const enrollAlarm = async (alarmTitle, alarmYN, alarmTime1, alarmTime2, alarmTime3, alarmDayStart, alarmDayEnd, alarmMediList, tagList, shareEmail) => {
  return await request
    .post( '/alarms', {  
      alarmTitle, alarmYN, alarmTime1, alarmTime2, alarmTime3, alarmDayStart, alarmDayEnd, alarmMediList, tagList, shareEmail
    })
    .then(response => {
      return response.data.alarmId;
    })
    .catch(err => {
      return err.response.data;
    });
};

export const userAPI = {
  login: async (userEmail, userPassword, userLoginType) => {
    return await request
      .post('/auth/login', {
        userEmail,
        userPassword,
        userLoginType,
      })
      .then(response => {
        AsyncStorage.setItem('refreshToken', response.data.refreshToken);
        setToken(response.data.accessToken);
        return response.data.statusCode;
      })
      .catch(error => {
        return error.response.status;
      });
  },
  social: async (userEmail, userPassword, userLoginType) => {
    return await request
      .post('/auth/social', {
        userEmail,
        userPassword,
        userLoginType,
      })
      .then(response => {
        AsyncStorage.setItem('refreshToken', response.data.refreshToken);
        setToken(response.data.accessToken);
      })
      .catch(error => {
        return error.response.status;
      });
  },
  join: async (
    userEmail,
    userPassword,
    userNickname,
    userProfileUrl,
    petName,
    userJoinType,
  ) => {
    return await request
      .post('/users/signin', {
        userEmail,
        userPassword,
        userNickname,
        userProfileUrl,
        petName,
        userJoinType,
      })
      .then(response => {
        return response.data.statusCode;
      })
      .catch(error => {
        return error.response.status;
      });
  },
  nickCheck: async userNickname => {
    return await request
      .get(`/users/nickname/${userNickname}`, {})
      .then(response => {
        return response.data.statusCode;
      })
      .catch(error => {
        return error.response.status;
      });
  },
  emailCheck: async userEmail => {
    return await request
      .get(`/users/email/${userEmail}`, {})
      .then(response => {
        return response.data.statusCode;
      })
      .catch(error => {
        return error.response.status;
      });
  },
};

export const getPharmacyAPI = async (lat, lon, week, curTime) => {
  return await request
    .get('/pharmacies', {
      params: {
        lat: lat,
        lon: lon,
        week: week,
        curTime: curTime,
      },
    })
    .then(response => {
      return response.data;
    })
    .catch(err => {
      return err.response.data;
    });
}

export const getMediListAPI = async searchKeyword => {
  return await request
    .get('/medicines/search', {
    params: {
      searchKeyword: searchKeyword,
    }
  })
  .then((response) => {
      return response.data.mediList;
  })
  .catch(err => {
    return err.response.data;
  });
}

export const getMediDetailAPI = async mediSerialNum => {
  return await request
    .get(`/medicines/detail/${mediSerialNum}`, {
    })
    .then((response) => {
      return response.data;
    })
    .catch(err => {
      return err.response.data;
    });
}

export const alarmCheckAPI = async (alarmId, thYN) => {
  return await request
  .post(`/alarms/check`,
    {
      alarmId,
      thYN
    }
  )
    .then((response) => {
      return response.data;
    })
    .catch(err => {
      return err.response.data;
    })
}

export const getMyPillAPI = async () => {
  return await request.get(`/mypills`, {
  })
    .then(response => {
      return response.data.alarmList;
    }).catch(err => {
      return err.response.data;
    });
}

export const getMyPillHistoryAPI = async pageNum => {
  return await request.get(`mypills/history`, {
    params: {
      pageNum: pageNum,
    }
  })
    .then(response => {
      return response.data;
    })
    .catch(err => {
      return err.response.data;
  })
}
