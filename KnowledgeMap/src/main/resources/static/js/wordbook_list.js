//登録用フォーム
const registForm = document.querySelector(".commonForm");
//登録用フォームの表示/非表示切り替えボタン
const showFormBtn = document.getElementById("showFormBtn");
//バリデーションエラー表示
const errorMsgList = document.getElementById("errorMsgList");


showFormBtn.addEventListener("click", () => {
	if (!registForm.classList.toggle("hidden")) {//hiddenが存在して削除したとき
		registForm.classList.add("visible");
	} else {
		registForm.classList.remove("visible");//hiddenが存在せず追加したとき	
	}
})
registForm.addEventListener("submit", async (event) => {
	event.preventDefault();
	errorMsgList.innerHTML = "";
	const wordbookName = document.getElementById("wordbookName").value.trim();
	const userId = document.getElementById("userId").value;
	const csrfToken = document.getElementById("csrfToken").value;
	try {
		const res = await fetch(`/wordbooks/api/regist`, {
			method: "POST",
			headers: {
				"Content-Type": "application/x-www-form-urlencoded",
				"X-CSRF-TOKEN": csrfToken
			},
			body: `wordbookName=${encodeURIComponent(wordbookName)}&userId=${userId}`
		});
		if (res.ok) {
			const wordbook = await res.json();
			addWordbookToList(wordbook.id, wordbook.wordbookName);
		} else {
			
			//バリデーションエラーがあるとき
			errorMsgList.classList.replace("errorMsgHidden","errorMsgVisible");
			const errorMessages = await res.json();
			for(const msg of errorMessages){
				const li = document.createElement("li");
				li.textContent = msg;
				errorMsgList.append(li);
			} 
		}
	} catch (error) {
		alert("登録に失敗しました");
	}
})
function addWordbookToList(wordbookId, wordbookName) {
	const wordbookList = document.querySelector(".wordbookList");
	const li = document.createElement("li");
	const a = document.createElement("a");
	a.href = `/wordbooks/${wordbookId}/words`;
	a.textContent = wordbookName;
	li.appendChild(a);
	wordbookList.insertBefore(li, wordbookList.firstElementChild);

	document.getElementById("wordbookName").value = "";

}

