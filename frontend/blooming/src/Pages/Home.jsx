import WeddingDday from "../components/Home/WeddingDday";
import PlanTips from "../components/Home/PlanTips";
import Tipbox from "../components/Home/TipBox";
import { useRecoilState, useResetRecoilState } from "recoil";
import { userState } from "../recoil/ProfileAtom";
import { customAxios } from "../lib/axios";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import classes from "./Home.module.css";

function Home() {
  const navigate = useNavigate();

  const [user, setUser] = useRecoilState(userState);
  const resetUserState = useResetRecoilState(userState);

  const setThemeState = (gender) => {
    const rootElement = document.documentElement;

    switch (gender) {
      case "MALE":
        rootElement.style.setProperty("--color-point", "var(--color-groom)");
        break;
      case "FEMALE":
        rootElement.style.setProperty("--color-point", "var(--color-brider)");
        break;
    }
  }

  const updateUser = async () => {
    try {
      // 유저 정보 조회
      const res = await customAxios.get("profile");
      if (res.data) {
        setUser(res.data.result[0]);
        if (res.data.result[0]?.gender) {
          setThemeState(res.data.result[0].gender)
        }
      }
    } catch (error) {
      // 유저 정보 초기화
      resetUserState();
      console.error("유저 정보 API 요청 에러", error);
      // navigate("/");
    }
  };

  useEffect(() => {
    updateUser();
  }, []);

  return (
    <div className={classes.container}>
      <WeddingDday />
    <div className={classes.top}>
      <PlanTips />
      {/* <p className={classes.word}> Wedding Tips</p>
      <hr className={classes.hr} />
      <Tipbox /> */}
      {/* <h1>---🚧🚦🚦🚨개발중👩🏻‍🚒👨🏻‍🚒💨🔨🚬---</h1> */}
    </div>
      
    </div>
  );
}

export default Home;
