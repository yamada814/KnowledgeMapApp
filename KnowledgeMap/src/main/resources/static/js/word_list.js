//新規登録や編集を実行後、対象wordのカテゴリとwordNameが選択された状態にする(色がつく)
window.addEventListener("DOMContentLoaded", async () => {
	const params = new URLSearchParams(window.location.search);
	const categoryId = params.get("categoryId");
	const wordId = params.get("id");
	if (categoryId) {
		setCategorySelection(categoryId);
		await showWordList(categoryId);
	}
	if (wordId) {
		setWordSelection(wordId)
		await showWordDetail(wordId);
	}
})
// 各コンテナ
const mainConteiner = document.querySelector(".mainContainer");
const categoryContainer = document.querySelector(".categoryContainer");
const wordListContainer = document.querySelector(".wordListContainer");
const wordDetailContainer = document.querySelector(".wordDetailContainer");
// カテゴリ
let currentCategoryId = null;
const categoryList = document.querySelector(".categoryList");
const categoryBtns = document.querySelectorAll(".categoryBtn");

categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		//選択中カテゴリの設定
		clearCategorySelection();
		currentCategoryId = categoryBtn.getAttribute("data-id");
		categoryBtn.classList.add("categoryBtnSelected");
		//単語一覧を表示
		wordListContainer.innerHTML = "";
		showWordList(currentCategoryId);
	})
})
// カテゴリ選択をクリア
function clearCategorySelection() {
	document.querySelectorAll(".categoryDeleteBtn").forEach(btn => btn.remove());
	document.querySelectorAll(".categoryBtnSelected").forEach(btn => btn.classList.remove("categoryBtnSelected"));
}
// 選択中カテゴリの色変更
function setCategorySelection(categoryId) {
	[...categoryBtns].find(btn => btn.getAttribute("data-id") === categoryId).classList.add("categoryBtnSelected");
}
// 選択中wordの色変更
function setWordSelection(wordId) {
	const wordBtns = document.querySelectorAll(".wordBtn");
	[...wordBtns].find(wordBtn => wordBtn.getAttribute("data-id") === wordId)
		.classList.add("wordBtnSelected");
}

// wordList表示
async function showWordList(categoryId) {
	try {
		const res = await fetch(`/api/words?categoryId=${categoryId}`);
		if (res.ok) {
			const words = await res.json();
			// 単語なしの場合
			if (words.length === 0) {
				// 「単語なし」メーセージ
				const msg = document.createElement("span");
				msg.textContent = "単語がありません";
				wordListContainer.appendChild(msg);
				// カテゴリ削除ボタン
				const categoryDeleteBtn = document.createElement("button");
				categoryDeleteBtn.classList.add("categoryDeleteBtn");
				const span = document.createElement("span");
				span.classList.add("bi-trash3-fill");
				categoryDeleteBtn.appendChild(span);
				//カテゴリボタンの横にカテゴリ削除ボタンを追加
				[...categoryBtns].find(btn => btn.getAttribute("data-id") === categoryId).after(categoryDeleteBtn);
				categoryDeleteBtn.addEventListener("click", (event) => deleteCategory(event, categoryId))

				// 単語ありの場合
			} else {
				const wordList = document.createElement("ul");
				wordList.classList.add("wordList");
				for (const word of words) {
					const li = document.createElement("li");
					// 単語ボタン
					const wordBtn = document.createElement("button");
					wordBtn.setAttribute("data-id", word.id);
					wordBtn.textContent = word.wordName;
					wordBtn.classList.add("wordBtn");
					li.appendChild(wordBtn);
					// 単語削除ボタン <button class="categoryDeleteBtn"><span class="bi-trash3-fill"></span></button>
					const wordDeleteBtn = document.createElement("button");
					wordDeleteBtn.classList.add("wordDeleteBtn");
					const span = document.createElement("span");
					span.classList.add("bi-trash3-fill");
					wordDeleteBtn.appendChild(span);

					wordDeleteBtn.addEventListener("click", (event) => {
						wordDetailContainer.innerHTML = "";
						deleteWord(event, word.id, li)
					});

					wordBtn.addEventListener("click", () => {
						wordDetailContainer.innerHTML = "";
						document.querySelectorAll(".wordBtnSelected").forEach(btn => btn.classList.remove("wordBtnSelected"))
						document.querySelectorAll(".wordDeleteBtn").forEach(btn => btn.remove());
						wordBtn.classList.add("wordBtnSelected");
						li.appendChild(wordDeleteBtn);

						showWordDetail(word.id);
					})
					li.appendChild(wordBtn);
					wordList.appendChild(li);
				}
				wordListContainer.appendChild(wordList);
			}
		}
	} catch (error) {
		console.log(error);
		const msg = document.createElement("p");
		msg.textContent = "取得に失敗しました";
		wordListContainer.appendChild(msg);
	}
}
//wordDetail表示
async function showWordDetail(wordId) {
	try {
		const res = await fetch(`/api/words/${wordId}`);
		if (res.ok) {
			const wordDetail = await res.json();

			const wordNameContainer = document.createElement("div");
			wordNameContainer.classList.add("wordNameContainer");
			//単語名
			const wordName = document.createElement("div");
			wordName.classList.add("wordName");
			wordName.textContent = wordDetail.wordName;
			
			//編集ボタン
			const editBtn = document.createElement("button");
			editBtn.classList.add("editBtn");
			const span = document.createElement("span");
			span.classList.add("bi-pencil-square");
			editBtn.append(span);
			editBtn.addEventListener("click", () => {
				location.href = `/words/${wordId}/editForm`;
			})
			wordNameContainer.append(wordName,editBtn);
			//カテゴリ
			const categoryContainer = document.createElement("div");
			categoryContainer.classList.add("category");

			const categoryLabel = document.createElement("span");
			categoryLabel.textContent = "カテゴリ：";
			const category = document.createElement("span");
			category.textContent = wordDetail.category.name;

			categoryContainer.append(categoryLabel, category);
			//説明文
			const content = document.createElement("div");
			content.classList.add("content");
			content.textContent = wordDetail.content;
			
			wordDetailContainer.append(wordNameContainer,categoryContainer,content);
			//関連語
			if (wordDetail.relatedWords && wordDetail.relatedWords.length > 0) {
				const relatedWordsContainer = document.createElement("div");
				relatedWordsContainer.classList.add("relatedWords")
				const reference = document.createElement("span");
				reference.textContent = "参照：";
				const relatedWords = document.createElement("ul");
				for (const word of wordDetail.relatedWords) {
					const li = document.createElement("li");
					li.textContent = word.wordName;
					relatedWords.appendChild(li);
				}
				relatedWordsContainer.append(reference, relatedWords);
				wordDetailContainer.appendChild(relatedWordsContainer);
			}
		}
	} catch (error) {
		console.log(error)
	}
}
//category削除
async function deleteCategory(event, categoryId) {
	event.stopPropagation();
	try {
		const res = await fetch(`/api/categories/${categoryId}`, { method: "DELETE" });
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
//word削除
async function deleteWord(event, wordId, li) {
	event.stopPropagation();
	try {
		const res = await fetch(`/api/words/${wordId}`, { method: "DELETE" });
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


































