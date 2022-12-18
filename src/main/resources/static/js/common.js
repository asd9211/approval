const commonError = (xhr, textStatus, errorThrown) =>{
    if(xhr.responseText == null || xhr.responseText == undefined || xhr.responseText == ''){
        alert("서버와의 연결에 실패했습니다. " + "에러[" + textStatus + "]" + errorThrown   );
    }else{
        let response = JSON.parse(xhr.responseText);
        alert( response.message  );
    }
}