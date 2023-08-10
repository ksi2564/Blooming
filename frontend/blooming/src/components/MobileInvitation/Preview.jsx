import { useRecoilValue } from 'recoil';
import { mobileInvitationState } from '../../recoil/MobileInvitationAtom';
import { useState, useEffect } from 'react';

import classes from './Preview.module.css';
import { Calendar } from 'antd-mobile';

function Preview({ onClose, positionStyle, showPre=true, showCloseButton=true }) {
  const invitationData = useRecoilValue(mobileInvitationState);
  const [dday, setDday] = useState(null);
  
  const calculateDday = (weddingDate) => {
    const now = new Date();
    const wedding = new Date(weddingDate.toISOString());
    const diff = wedding - now;
    const dday = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return dday;
  };

  useEffect(() => {
    if (invitationData.date) {
      setDday(calculateDday(invitationData.date));
    } else {
      setDday(null);
    }
  }, [invitationData.date]);

  return (
    <div className={classes.total} style={positionStyle}>
      {showPre && <p className={classes.pre}>미리보기</p> }
      {showCloseButton && <button onClick={onClose}>X</button> }

      {/* 진짜 내용 */}
      <div className={classes.form}>

      {/* ---------메인-------- */}
      <div className={classes.main}>
        <p className={classes.mainTitle}>WEDDING DAY</p>
        <p className={classes.mainDday}>{dday ? "D-" + dday : "D-Day"}</p>
        <img
          src={invitationData.thumbnail ? invitationData.thumbnail : '../../../src/assets/Character/main.jpeg'}
          alt="thumbnail"
        />
        <p className={classes.mainName}>{invitationData.groomName ? invitationData.groomName : '신랑'} <span style={{fontSize:'15px'}}>그리고</span> {invitationData.brideName ? invitationData.brideName : '신부'}</p>
        {/* <img src={Ring} alt="Ring Icon" style={{margin:'10px 0'}}/> */}
        <p className={classes.mainWedding}>
          {invitationData.date
            ? invitationData.date
            : '예식일'}{' '}
          <br />
          {invitationData.weddingHallName
            ? invitationData.weddingHallName
            : '예식장 명'}{' '}
          &nbsp; | &nbsp;{' '}
          {invitationData.floor ? invitationData.floor : '예식장 층 및 홀'}
        </p>
        <hr />        
      </div>

      {/* --------인사말---------- */}
      <div className={classes.mention}>
        <p className={classes.mentionTitle}> 🌿 {invitationData.title ? invitationData.title : 'Invitation'} 🌿</p>
        <p className={classes.mentionContent}>{invitationData.content ? invitationData.content : '서로가 마주보며 다져온 사랑을 이제 함께 한 곳을 바라보며 걸어갈 수 있는 큰 사랑으로 키우고자 합니다. 저희 두 사람이 사랑의 이름으로 지켜나갈 수 있도록 앞날을 축복해 주시면 감사하겠습니다.'} </p>
        <hr />
      </div>


      {/* --------연락 관련-------- */}
      <div className={classes.connect}>
        <div className={classes.connectName}>
          {invitationData.groomFatherName ? invitationData.groomFatherName : '신랑아버지'} ∘ {invitationData.groomMotherName ? invitationData.groomMotherName : '신랑어머니'} <span style={{fontSize:'12px'}}>의 아들</span> {invitationData.groomName ?invitationData.groomName : '신랑'} <br />
          {invitationData.brideFatherName ? invitationData.brideFatherName : '신부아버지'} ∘ {invitationData.brideMotherName ? invitationData.brideMotherName :  '신부어머니'} <span style={{fontSize:'12px'}}>의 딸</span> {invitationData.brideName ? invitationData.brideName : '신부'}
          <div className={classes.connectImg}>소중한 당신을 초대합니다</div>
        </div>

        <div className={classes.connectCouple}>
          <p>신랑에게 연락하기 </p>
          <p>신부에게 연락하기 </p>
        </div>

        <div className={classes.connectParent}>
          <div className={classes.connectParentImg}>혼주에게 연락하기</div>
          
          <div className={classes.connectParentPhone}>
            <p>신랑 측 혼주 <br /> 
            아버지 <span style={{fontWeight:'bold'}}>{invitationData.groomFatherName ? invitationData.groomFatherName : '신랑아버지'}</span> <br /> 
            어머니 <span style={{fontWeight:'bold'}}>{invitationData.groomMotherName ? invitationData.groomMotherName : '신랑어머니'} </span> <br /> 
            </p>

            <p>신부 측 혼주 <br /> 
            아버지 <span style={{fontWeight:'bold'}}>{invitationData.brideFatherName ? invitationData.brideFatherName : '신부아버지'}</span> <br /> 
            어머니 <span style={{fontWeight:'bold'}}>{invitationData.brideMotherName ? invitationData.brideMotherName : '신부어머니'}</span> <br /> 
            </p>
          </div>

        </div>
      </div>

      {/* 캘린더 - 수정해야함 */}
      <div className={classes.calendar}>
        <Calendar />
      </div>
    </div>
    </div>
  );
}

export default Preview;
