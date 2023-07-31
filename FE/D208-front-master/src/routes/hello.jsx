import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

export default function Hello() {
  const dispatch = useDispatch();
  const count = useSelector((state) => state);

  const increment = () => {
    dispatch({ type: "INCREMENT" });
  };

  const decrement = () => {
    dispatch({ type: "DECREMENT" });
  };

  useEffect(() => {
    // 상태가 변경될 때마다 실행되는 콜백 함수
    console.log(count);
  }, [count]); // count가 변경될 때마다 콜백 함수 실행

  return (
    <>
      <div>
        <p>im router</p>
      </div>
      <p>Count: {count}</p>
      <button onClick={increment}>Increment</button>
      <button onClick={decrement}>Decrement</button>
    </>
  );
}
