// firstmain.jsx
import { useState, useEffect } from "react";
import Modal from 'react-modal';
import "../../css/presetRegisterModal.css"
import Konva from "konva";

function PresetRegistModal(props){
    // const [imgUrl, setImgUrl] = useState(null);

    function insertPreset(stage){
        console.log("등록하기 입력!");
        if(stage == null){
            console.log("등록할 캔버스가 없습니다.");
            return;
        }

        // let formData = new FormData();

        console.log(stage.toJSON());
        // stage.children[2].removeChildren();
        // console.log(stage.toJSON());
        Konva.Image.fromURL(stage.toDataURL(), img => {
            console.log(img);
        });

        // setImgUrl(imgData);

        // var binaryData = atob(imgData.split(',')[1]);
        // var array = [];

        // console.log(stage.toJSON());

        // formData.append('stage',stage.toJSON());

        // for (var i = 0; i < binaryData.length; i++) {
        //     array.push(binaryData.charCodeAt(i));
        // }

        // filename = document.querySelector("#presetName").value;

        // file = new File([new Uint8Array(array)], filename+".png", {type: 'image/png'});
    }

    return(
        <Modal isOpen={props.isOpen}
               onRequestClose={()=>{
                   props.onChange();
               }}
               style={{
                   overlay: {
                       position: 'fixed',
                       zIndex: 1020,
                       top: 0,
                       left: 0,
                       width: '100vw',
                       height: '100vh',
                       background: 'rgba(0, 0, 0, 0.5)',
                       display: 'flex',
                       alignItems: 'center',
                       justifyContent: 'center',
                   },
                   content: {
                       background: 'white',
                       width: '80%',
                       maxWidth: '1440px',
                       maxHeight: '1024',
                       overflowY: 'auto',
                       position: 'relative',
                       border: '1px solid #ccc',
                       borderRadius: '0.3rem',
                   }}}>
            <div className = "modal-container">
                <div className="modal-title">프리셋 등록하기</div>
                <div className="modal-form">
                    <div className="modal-input">
                        <div className="modal-input-title">프리셋 이름</div>
                        <input className="modal-input-content"></input>
                    </div>
                    <div className="modal-input">
                        <div className="modal-input-title">프리셋 미리보기</div>
                        <img className="modal-input-image" src={imgUrl}></img>
                    </div>
                </div>
                <button type="button" onClick={insertPreset(props.stage)}>등록하기</button>
            </div>
        </Modal>
    );
}

export default PresetRegistModal;
