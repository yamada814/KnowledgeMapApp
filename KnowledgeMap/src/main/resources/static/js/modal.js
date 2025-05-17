//削除確認モーダル
const modalOverlay = document.getElementById("modalOverlay");
const modalOuter = document.getElementById("modalOuter");
const deleteConfirmModal = document.getElementById("deleteConfirmModal");
const deletedMsgModal = document.getElementById("deletedMsgModal");
//削除確認モーダル内のボタン
const deleteOkBtn = document.getElementById("deleteOk");
const deleteNgBtn = document.getElementById("deleteNg");
//モーダル外側をクリックしたときに発生するイベントハンドラ(モーダルを閉じる処理を行う)
let eventHandler = null;
//モーダルの はい/いいえ ボタンをクリックしたときに発生するイベントハンドラ(削除実行を行う関数にtrue/falseを渡す)
let deleteOkFunc = null;
let deleteNgFunc = null;


/**
 * モーダルを表示する共通関数
 * @param {Event} event - イベントオブジェクト
 * @param {HTMLElement} triggerEl - 削除ボタン
 * @param {Function} func - 真偽値を引数として受け取りtrueの場合はサーバへ削除リクエストを送信する関数
 * @param {Object} [options] - オプション設定
 */

export function showModal({ triggerEl, func, options = {} }) {
	modalOuter.classList.remove("hidden");
	modalOverlay.classList.remove("hidden");

	// モーダルの表示位置を設定
	const rect = triggerEl.getBoundingClientRect();
	const modalTop = rect.bottom + scrollY + 8;
	const modalLeft = rect.right - modalOuter.offsetWidth;
	//CSS変数に値をセット
	document.documentElement.style.setProperty('--modal-top', `${modalTop}px`);
	document.documentElement.style.setProperty('--modal-left', `${modalLeft}px`);
	
	console.log(options.selectedTargerSelecrtor);
	// wordbook_listにて対象となる li > a 要素を、選択中として色変更
	if (options.selectedTargetSelector) {
		const selectedEl = triggerEl.closest("li").querySelector(options.selectedTargetSelector);
		console.log(selectedEl);
		if (selectedEl) {
			selectedEl.classList.add("selected");
		}
	}

	// OKボタンクリック -> func(true)を実行してモーダルを閉じる
	deleteOkFunc = (e) => {
		e.preventDefault();
		e.stopPropagation();
		func(true);
	}
	deleteOk.addEventListener("click", deleteOkFunc);

	// NGボタンクリック -> func(false)を実行してモーダルを閉じる
	deleteNgFunc = (e) => {
		e.preventDefault();
		e.stopPropagation();
		func(false);
		closeModal();
	}
	deleteNg.addEventListener("click", deleteNgFunc);

	//モーダルの外側をクリックするとモーダルを閉じる関数
	eventHandler = (e) => {
		if (!modalOuter.contains(e.target)) {
			closeModal();
		}
	}
	//モーダルを閉じる関数をクリックイベントに登録
	document.addEventListener("click", eventHandler);
}
// 削除完了メッセージを表示する
export function showDeletedMsg(modalMsg) {
	if (!deleteConfirmModal.classList.contains("hidden")) {
		deleteConfirmModal.classList.add("hidden");
	}
	if (deletedMsgModal.classList.contains("hidden")) {
		deletedMsgModal.classList.remove("hidden");
	}
	document.getElementById("deletedMsg").textContent = modalMsg;
	document.getElementById("closeModalBtn").addEventListener("click", closeModal);
}

// 削除確認モーダルを閉じる
export function closeModal() {
	if (!modalOuter.classList.contains("hidden")) {
		modalOuter.classList.add("hidden");
		modalOverlay.classList.add("hidden");
			
		const selectedEl = document.querySelector(".selected")
		if(selectedEl){
			selectedEl.classList.remove("selected");
		}

		//画面全体のクリックイベント削除
		if (eventHandler) {
			document.removeEventListener("click", eventHandler);
			eventHandler = null;
		}
		//はい/いいえボタンのクリックイベント削除
		if (deleteOkFunc) {
			deleteOk.removeEventListener("click", deleteOkFunc);
			deleteOkFunc = null;
		}
		if (deleteNgFunc) {
			deleteNg.removeEventListener("click", deleteNgFunc);
			deleteNgFunc = null;
		}
	}
}