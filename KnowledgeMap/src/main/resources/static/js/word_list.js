
let selectedCategoryId = null;//現在表示されているカテゴリのidを保持する用
const wordList = document.getElementById("wordList");
const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordName = document.getElementById("detail-wordName");
const content = document.getElementById("detail-content");
const category = document.getElementById("detail-category");
const relatedWords = document.getElementById("relatedWords");
const editBtnContainer = document.getElementById("editBtnContainer");
const wordDetailContainer = document.querySelector(".wordDetailHidden");

//カテゴリ名をクリック -> そのカテゴリに属するword一覧を表示
categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		wordList.innerHTML = "";//word一覧をクリア
		clearWordDetail();//wordDetailの内容をクリア
		const categoryId = categoryBtn.getAttribute("data-id");
		selectedCategoryId = categoryId;
		showWordList(categoryId);
	})
});
//編集画面から戻ってきた時に前画面の内容を表示させる
window.addEventListener("DOMContentLoaded", async () => {
	const params = new URLSearchParams(window.location.search);
	const categoryId = params.get("categoryId");
	const wordId = params.get("id");
	console.log(categoryId,wordId);
	if (categoryId) {
		await showWordList(categoryId);
	}
	if (wordId) {
		await showWordDetail(wordId);
	}
});
//wordDetailの項目をクリアする
function clearWordDetail() {
	wordName.innerHTML = "";
	content.innerHTML = "";
	category.innerHTML = "";
	relatedWords.innerHTML = "";
	editBtnContainer.innerHTML = "";
	wordDetailContainer.classList.replace("wordDetailVisible","wordDetailHidden");

}
//カテゴリに属するword一覧を表示する
async function showWordList(categoryId) {
	try {
		const res = await fetch(`/api/words?categoryId=${categoryId}`);
		if (res.ok) {
			const words = await res.json();
			//wordがない場合
			if (words.length === 0) {
				const msg = document.createElement("p");
				msg.textContent = "単語がありません";
				wordList.appendChild(msg);
				//カテゴリ削除ボタンを生成
				const categoryDeleteBtn = document.createElement("button");
				categoryDeleteBtn.textContent = "このカテゴリを削除";
				categoryDeleteBtn.addEventListener("click", () => deleteCategory(selectedCategoryId));
				wordList.appendChild(categoryDeleteBtn);
				
				return;
			}
			//wordがある場合
			const ul = document.createElement("ul");
			for (const word of words) {
				const li = document.createElement("li");
				li.textContent = word.wordName;
				//word削除ボタンの作成
				const wordDeleteBtn = document.createElement("button");
				wordDeleteBtn.textContent = "削除";
				wordDeleteBtn.addEventListener("click", (e) => deleteWord(e, word.id, li))
				li.appendChild(wordDeleteBtn);
				//詳細表示
				li.addEventListener("click", () => showWordDetail(word.id));
				ul.appendChild(li);
			}
			wordList.appendChild(ul);
		}
	} catch (error) {
		console.error(error);
		const msg = document.createElement("p");
		msg.textContent = "取得に失敗しました";
		wordList.appendChild(msg);
	}
}
//カテゴリを削除する(カテゴリをクリックしてwordがなかった時)
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
//word詳細を表示する関数
async function showWordDetail(id) {
	clearWordDetail();
	try {
		const res = await fetch(`/api/words/${id}`);
		if (res.ok) {
			const wordDetail = await res.json();
			wordName.textContent = wordDetail.wordName;
			content.textContent = wordDetail.content;
			category.textContent = wordDetail.category.name;

			for (const relatedWord of wordDetail.relatedWords) {
				const li = document.createElement("li");
				li.textContent = relatedWord.wordName;
				relatedWords.appendChild(li);
			}
			// 編集ボタンを作成
			const editBtn = document.createElement("button");
			editBtn.textContent = "編集";
			editBtn.addEventListener("click", () => {
				location.href = `/words/${wordDetail.id}/editForm`;
			});
			editBtnContainer.appendChild(editBtn);
		}
		wordDetailContainer.classList.replace("wordDetailHidden","wordDetailVisible");
	} catch (error) {
		console.error(error);
		alert("詳細情報の取得に失敗しました" + id);
	}
}
//word削除する関数
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




