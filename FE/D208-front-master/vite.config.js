import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // server: {
  //   host: "127.0.0.1",
  // },
  resolve: {
    alias: {
      // SockJS 모듈을 사용할 수 있도록 에일리어스 설정
      "sockjs-client": "sockjs-client/dist/sockjs.min.js",
    },
  },
});
