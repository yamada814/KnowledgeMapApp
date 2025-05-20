import { showModal, showDeletedMsg } from "./modal.js";

//登録用フォーム
const registForm = document.querySelector(".commonForm");
//登録用フォームの表示/非表示切り替えボタン
const showFormBtn = document.getElementById("showFormBtn");
//バリデーションエラー表示
const errorMsgList = document.getElementById("errorMsgList");
//CSRFトークンの取得
const csrfToken = document.getElementById("csrfToken").value;
// 削除実行後にモーダルに表示する処理結果メッセージ
let modalMsg;


/*
単語帳の登録フォーム表示
*/
showFormBtn.addEventListener("click", () => {
	if (!registForm.classList.toggle("hidden")) {//hiddenが存在して削除したとき
		registForm.classList.add("visible");
	} else {
		registForm.classList.remove("visible");//hiddenが存在せず追加したとき	
	}
})
/*
単語帳の登録処理実行
*/
registForm.addEventListener("submit", async (event) => {
	event.preventDefault();
	errorMsgList.innerHTML = "";
	const wordbookName = document.getElementById("wordbookName").value.trim();
	const userId = document.getElementById("userId").value;//バリデータ用
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
			errorMsgList.classList.remove("errorMsgHidden");
			errorMsgList.classList.add("errorMsgVisible");

			const errorMessages = await res.json();
			for (const msg of errorMessages) {
				const li = document.createElement("li");
				li.textContent = msg;
				errorMsgList.append(li);
			}
		}
	} catch (error) {
		alert("登録に失敗しました");
	}
})
//単語帳リストにDOMを追加する関数
function addWordbookToList(wordbookId, wordbookName) {

	const wordbookList = document.querySelector(".wordbookList");
	const li = document.createElement("li");
	const a = document.createElement("a");

	a.href = `/wordbooks/${wordbookId}/words`;
	a.textContent = wordbookName;
	li.appendChild(a);

	const form = document.createElement("form");
	form.className = "buttonContainer";
	form.innerHTML = `
		<button class="deleteBtn" data-id="${wordbookId}"><span class="bi-trash3-fill"></span></button>
		`;
	li.appendChild(form);

	wordbookList.insertBefore(li, wordbookList.firstElementChild);

	document.getElementById("wordbookName").value = "";
}

/*
単語帳の削除
	
(動的に追加される単語帳に対してもイベントを付与するため、親要素にイベントを登録する)
*/


document.querySelector(".wordbookList").addEventListener("click", async (event) => {
	const deleteBtn = event.target.closest(".deleteBtn");
	if (!deleteBtn) return;

	event.stopPropagation();
	event.preventDefault();//documentへのイベント伝播によるcloseModal()の即実行を防ぐ

	// モーダルの表示
	showModal({
		triggerEl: deleteBtn,
		func: 
		async (isConfirmed) => {
			const id = deleteBtn.dataset.id;
			const csrfToken = document.getElementById("csrfToken").value;
			if (!isConfirmed) {
				return;
			}
			try {
				const res = await fetch(`/wordbooks/api/delete/${id}`,
					{
						method: "DELETE",
						headers: { "X-CSRF-TOKEN": csrfToken }
					});
				if (res.ok) {
					deleteBtn.closest('li').remove();
					modalMsg = "削除しました";
				} else {
					const errorMsg = await res.json();
					modalMsg = errorMsg.error;
				}
			} catch (error) {
				console.log(error);
				modalMsg = "削除に失敗しました";
			}
			
			showDeletedMsg(modalMsg);
		},
		options: {
			selectedTargetSelector: "a"
		}
	})
})

