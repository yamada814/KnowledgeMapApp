
let selectedCategoryId = null;//現在表示されているカテゴリのidを保持する用
const wordListContainer = document.querySelector(".wordListContainer");
const wordList = document.getElementById("wordList");
const wordListNullMsg = document.getElementById("wordListNullMsg")
const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordName = document.getElementById("detail-wordName");
const content = document.getElementById("detail-content");
const category = document.getElementById("detail-category");
const relatedWords = document.getElementById("detail-relatedWords");
const reference = document.querySelector(".reference");
const relatedWordList = document.getElementById("relatedWordList");
const editBtnContainer = document.getElementById("editBtnContainer");
const wordDetailContainer = document.querySelector(".wordDetailHidden");

//カテゴリをクリック
categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		//もろもろクリアする
		document.querySelectorAll(".categoryDeleteBtn").forEach(btn => btn.remove());
		document.querySelectorAll(".categoryBtnSelected").forEach(btn => btn.classList.remove("categoryBtnSelected"));
		wordList.innerHTML = "";
		wordListNullMsg.innerHTML = "";
		clearWordDetail();
		//現在選択中のcategoryIdを記憶
		const categoryId = categoryBtn.getAttribute("data-id");
		selectedCategoryId = categoryId;
		categoryBtn.classList.add("categoryBtnSelected");
		//wordList表示
		showWordList(categoryId, categoryBtn);
	})
});

//wordDetailの項目をクリア
function clearWordDetail() {
	wordName.innerHTML = "";
	content.innerHTML = "";
	category.innerHTML = "";
	relatedWordList.innerHTML = "";
	editBtnContainer.innerHTML = "";
	wordDetailContainer.classList.replace("wordDetailVisible", "wordDetailHidden");
	if (reference) {
		reference.classList.replace("referenceVisible", "referenceHidden");
	}
}
//wordListの表示
async function showWordList(categoryId, categoryBtn) {
	try {
		const res = await fetch(`/api/words?categoryId=${categoryId}`);
		if (res.ok) {
			const words = await res.json();
			//wordがない場合
			if (words.length === 0) {
				const msg = document.createElement("p");
				msg.textContent = "単語がありません";
				wordListNullMsg.appendChild(msg);
				//カテゴリ削除ボタンを生成
				const categoryDeleteBtn = document.createElement("button");
				categoryDeleteBtn.classList.add("categoryDeleteBtn");
				const span = document.createElement("span");
				span.classList.add("bi", "bi-trash3-fill");
				categoryDeleteBtn.appendChild(span);
				categoryDeleteBtn.addEventListener("click", () => deleteCategory(selectedCategoryId));
				categoryBtn.after(categoryDeleteBtn);

				return;
			}
			//wordがある場合
			for (const word of words) {
				const li = document.createElement("li");
				const wordButton = document.createElement("button");
				wordButton.textContent = word.wordName;
				wordButton.classList.add("wordBtn");
				li.appendChild(wordButton);
				//削除ボタンと詳細表示
				wordButton.addEventListener("click", () => {
					//削除ボタンをいったん全削除
					document.querySelectorAll(".wordDeleteBtn").forEach(btn => btn.remove());
					document.querySelectorAll(".wordBtnSelected").forEach(btn => btn.classList.remove("wordBtnSelected"));

					wordButton.classList.add("wordBtnSelected");
					//word削除ボタンの作成
					const currentWordDeleteBtn = document.createElement("button");
					const span = document.createElement("span");
					span.classList.add("bi", "bi-trash3-fill");
					currentWordDeleteBtn.appendChild(span);
					currentWordDeleteBtn.classList.add("wordDeleteBtn");
					currentWordDeleteBtn.addEventListener("click", (e) => deleteWord(e, word.id, li))
					li.appendChild(currentWordDeleteBtn);

					//wordDetail表示
					showWordDetail(word.id)
				});
				wordList.appendChild(li);
			}
		}
	} catch (error) {
		console.error(error);
		const msg = document.createElement("p");
		msg.textContent = "取得に失敗しました";
		wordListContainer.appendChild(msg);
	}
}
//カテゴリの削除
async function deleteCategory(id) {
	try {
		const res = await fetch(`/api/categories/${id}`, { method: "DELETE" });
		if (res.ok) {
			location.reload();
		} else {
			alert("削除に失敗しました");
		}
	} catch (error) {
		console.error(error);
		alert("通信エラーが発生しました");
	}
}
//word詳細の表示
async function showWordDetail(id) {
	clearWordDetail();
	try {
		const res = await fetch(`/api/words/${id}`);
		if (res.ok) {
			const wordDetail = await res.json();
			wordName.textContent = wordDetail.wordName;
			content.textContent = wordDetail.content;
			category.textContent = wordDetail.category.name;

			if (wordDetail.relatedWords && wordDetail.relatedWords.length > 0) {
				if (reference) {
					reference.classList.replace("referenceHidden", "referenceVisible");
				}
				for (const relatedWord of wordDetail.relatedWords) {
					const li = document.createElement("li");
					li.textContent = relatedWord.wordName;
					relatedWordList.appendChild(li);
				}
			}
			// 編集ボタンを作成
			const editBtn = document.createElement("button");
			editBtn.textContent = "編集";
			const span = document.createElement("span");
			span.classList.add("bi", "bi-pencil-square");
			editBtn.appendChild(span);
			editBtn.addEventListener("click", () => {
				location.href = `/words/${wordDetail.id}/editForm`;
			});
			editBtnContainer.appendChild(editBtn);
		}
		wordDetailContainer.classList.replace("wordDetailHidden", "wordDetailVisible");
	} catch (error) {
		console.error(error);
		alert("詳細情報の取得に失敗しました" + id);
	}
}
//wordの削除
async function deleteWord(e, id, li) {
	e.stopPropagation();
	try {
		const res = await fetch(`/api/words/${id}`, { method: "DELETE" });
		if (res.ok) {
			li.remove();
			clearWordDetail()
		} else {
			alert("削除に失敗しました");
		}
	} catch (error) {
		console.log(error);
	}
}
//編集画面から戻ってきた時に前画面の内容を表示させる
window.addEventListener("DOMContentLoaded", async () => {
	const params = new URLSearchParams(window.location.search);
	const categoryId = params.get("categoryId");
	const wordId = params.get("id");
	console.log(categoryId, wordId);
	if (categoryId) {
		await showWordList(categoryId);
	}
	if (wordId) {
		await showWordDetail(wordId);
	}
});




