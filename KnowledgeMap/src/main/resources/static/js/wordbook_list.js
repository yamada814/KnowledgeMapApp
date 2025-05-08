document.addEventListener("DOMContentLoaded", () => {
	//登録用フォーム
	const registForm = document.querySelector(".commonForm");
	//登録用フォームの表示/非表示切り替えボタン
	const showFormBtn = document.getElementById("showFormBtn");
	//バリデーションエラー表示
	const errorMsgList = document.getElementById("errorMsgList");


	//「単語帳を追加」ボタンをクリックすると フォーム表示
	showFormBtn.addEventListener("click", () => {
		if (!registForm.classList.toggle("hidden")) {//hiddenが存在して削除したとき
			registForm.classList.add("visible");
		} else {
			registForm.classList.remove("visible");//hiddenが存在せず追加したとき	
		}
	})
	//「追加する」ボタンをクリックすると 登録処理実行
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
				errorMsgList.classList.replace("errorMsgHidden", "errorMsgVisible");
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
	//単語帳リストに追加する
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
		<input type="hidden" id="csrfToken" value="${document.getElementById("csrfToken").value}" />
		<button class="deleteBtn" data-id="${wordbookId}"><span class="bi-trash3-fill"></span></button>
		`;
		li.appendChild(form);
		wordbookList.insertBefore(li, wordbookList.firstElementChild);

		document.getElementById("wordbookName").value = "";
		console.log("delete button added");
	}

	const deleteConfirmModal = document.getElementById("deleteConfirmModal");
	const deleteOk = document.getElementById("deleteOk");
	const deleteNg = document.getElementById("deleteNg");


	//削除ボタンをクリックすると 削除実行(イベントデリゲーションのため、親要素にイベント付与)
	document.querySelector(".wordbookList").addEventListener("click", async (event) => {
		const deleteBtn = event.target.closest(".deleteBtn");
		if (!deleteBtn) return;
		event.stopPropagation();
		event.preventDefault();//documentへのイベント伝播によるcloseModal()の即実行を防ぐ
		showModal(deleteBtn, async (isConfirmed) => {
			const id = deleteBtn.dataset.id;
			const csrfToken = document.getElementById("csrfToken").value;
			if (!isConfirmed) {
				console.log("funcにfalseが渡った")
				return;
			}
			console.log("funcにtrueが渡った")
			try {
				const res = await fetch(`/wordbooks/api/delete/${id}`,
					{
						method: "DELETE",
						headers: { "X-CSRF-TOKEN": csrfToken }
					});
				if (res.ok) {
					deleteBtn.closest('li').remove();
				}
			} catch (error) {
				console.log(error);
			}
		})
	})

	function showModal(deleteBtn, func) {
		// モーダルの表示
		deleteConfirmModal.classList.remove("modalHidden");
		// モーダルの表示位置を設定
		const rect = deleteBtn.getBoundingClientRect();
		const modalTop = rect.bottom + scrollY + 8;
		const modalLeft = rect.right - deleteConfirmModal.offsetWidth;

		document.documentElement.style.setProperty('--modal-top', `${modalTop}px`);
		document.documentElement.style.setProperty('--modal-left', `${modalLeft}px`);

		// OKボタンクリック -> func(true)を実行してモーダルを閉じる
		deleteOk.addEventListener("click", (event) => {
			event.preventDefault();
			event.stopPropagation();
			func(true);
			closeModal();
		})

		// NGボタンクリック -> func(false)を実行してモーダルを閉じる
		deleteNg.addEventListener("click", (event) => {
			event.preventDefault();
			event.stopPropagation();
			func(false);
			closeModal();
		})

		//モーダルの外側をクリックするとモーダルを閉じる関数
		eventHandler = (event) => {
			if (!deleteConfirmModal.contains(event.target)) {
				closeModal();
			}
		}
		//上記関数をクリックイベントに登録
		document.addEventListener("click", eventHandler);
	}

	function closeModal() {
		console.log("closeModal実行");
		deleteConfirmModal.classList.add("modalHidden");
		if (eventHandler) {
			document.removeEventListener("click", eventHandler);
			eventHandler = null;
		}
	}
})


