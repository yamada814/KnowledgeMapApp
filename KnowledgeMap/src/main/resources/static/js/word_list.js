let selectedCategoryId = null;//現在表示されているカテゴリのidを保持する用
const wordContainer = document.getElementById("wordContainer");
const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordName = document.getElementById('detail-wordName');
const content = document.getElementById('detail-content');
const category = document.getElementById('detail-category');
const relatedWordList = document.getElementById('relatedWordList');
//wordDetailの項目をクリアする
function clearWordDetail() {
	wordName.innerHTML = "";
	content.innerHTML = "";
	category.innerHTML = "";
	relatedWordList.innerHTML = "";
}
//カテゴリを削除する関数
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
				relatedWordList.appendChild(li);
			}
		}
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
//カテゴリ名をクリック -> そのカテゴリに属するword一覧を表示
categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		wordContainer.innerHTML = "";
		clearWordDetail()
		//カテゴリボタンのdata-id属性の値からcategoryIdを取得して、現在表示されているカテゴリの情報を保持
		const categoryId = categoryBtn.getAttribute("data-id");
		selectedCategoryId = categoryId;
		try {
			const res = await fetch(`/api/words?categoryId=${categoryId}`);
			if (res.ok) {
				const words = await res.json();
				//wordがない場合
				if (words.length === 0) {
					const msg = document.createElement("p");
					msg.textContent = "単語がありません";
					wordContainer.appendChild(msg);
					//カテゴリ削除ボタンを生成
					const categoryDeleteBtn = document.createElement("button");
					categoryDeleteBtn.textContent = "このカテゴリを削除";
					categoryDeleteBtn.addEventListener("click", () => deleteCategory(selectedCategoryId));
					wordContainer.appendChild(categoryDeleteBtn);
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
				wordContainer.appendChild(ul);
			}
		} catch (error) {
			console.error(error);
			const msg = document.createElement("p");
			msg.textContent = "取得に失敗しました";
			wordContainer.appendChild(msg);
		}
	})
});



