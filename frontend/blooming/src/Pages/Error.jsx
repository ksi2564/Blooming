import ErrorModal from "../components/Error/ErrorModal";
import useErrorModal from "../components/Error/useErrorModal";
import IconList from "../components/Icons/IconList";

import Modal from "../components/Error/Modal";
import { useCallback, useState } from "react";
import { useRecoilState } from "recoil";
import axios from "axios";
import { errorState } from "../recoil/ErrorAtom";
import { GenderButton } from "../components/Common/GenderButton";

const Error = () => {
  // 에러 모달
  // const [ErrorModal, handleError] = useErrorModal();
  // const onClickButton = (event) => {
  //   handleError("데이터 요청 에러");
  // };

  // 모달 테스트
  const [isModalOpen, setIsModalOpen] = useState(false);
  const openModal = () => setIsModalOpen(true);
  // const onClose = () => setIsModalOpen(false);

  // 에러 모달 테스트
  const [errorModal, setErrorModal] = useRecoilState(errorState);
  const triggerError = useCallback(async () => {
    try {
      await axios.get("https://non-existing-url.com/api/data");
    } catch (error) {
      console.log(error);
      setErrorModal(true);
    }
  }, [setErrorModal]);

  return (
    <div className='mainContainer'>
      <p>여긴 사공사</p>

      <div>
        <h1>폰트 테스트다</h1>
        <div style={{ fontSize: "16px" }}>16 이건 어때?</div>
        <div style={{ fontSize: "18px" }}>18 이건 어때?</div>
        <div style={{ fontSize: "20px" }}>20 이건 어때?</div>
        <div style={{ fontSize: "22px" }}>22 이건 어때?</div>
        <div style={{ fontSize: "24px" }}>24 이건 어때?</div>
        <div style={{ fontSize: "26px" }}>26 이건 어때?</div>
        <div style={{ fontSize: "28px" }}>28 이건 어때?</div>
        <div style={{ fontSize: "30px" }}>30이건 어때?</div>
      </div>
      {/* 왜안돼 */}
      {/* <button onClick={onClickButton}>나와라모달</button>
      <ErrorModal /> */}

      {/* 모달 테스트 */}
      <button onClick={openModal}>나와라모달</button>
      <Modal
        buttonText={"닫기"}
        show={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      >
        <h2>나는모달</h2>
        <p>나는모달내용</p>
      </Modal>
      <button onClick={triggerError}>나와라에러</button>
      <Modal
        buttonText={"뒤로가기"}
        show={errorModal}
        onClose={() => setErrorModal(false)}
      >
        <h2>Error</h2>
        <p>에러등장</p>
        <button>닫기</button>
      </Modal>

      {/* <GenderButton /> */}

      <div style={{ border: "1px solid black" }}>
        <h2>아이콘리스트다</h2>
        <IconList />
      </div>
    </div>
  );
};

export default Error;
