import { customAxios } from "../../lib/axios";
import { useRecoilState } from "recoil";
import { wishlistState, fianceWishlistState } from "../../recoil/ProfileAtom";
import { useEffect } from "react";

export default function MyWishlist() {

  const [myWishlist, setMyWishlist] = useRecoilState(wishlistState)
  const [fianceWishlist, setFianceWishlist] = useRecoilState(fianceWishlistState)

  const fetchData = async () => {
    try {
      const response = await customAxios.get("wishlist");
      setMyWishlist(response.data.result[0].mywishlist)
      setFianceWishlist(response.data.result[0].couplewishlist)
    } catch (error) {
      console.error("예약 정보 조회 에러:", error);
    }
  };

  useEffect(() => {
    fetchData()
  }, [])

  return (
    <div style={{marginTop: '56px'}}>
      {myWishlist}
      {fianceWishlist}
    </div>
  );
}