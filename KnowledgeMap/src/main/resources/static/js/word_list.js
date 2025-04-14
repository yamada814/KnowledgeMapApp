//現在表示されているカテゴリのidを保持する用
let selectedCategoryId = null;

const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordList = document.getElementById("wordList");
const deleteBtn = document.querySelector(".deleteBtnHidden");

//カテゴリ毎のword表示
categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		wordList.innerHTML = "";
		//data-id属性にはcategoryIdが設定されている
		const categoryId = categoryBtn.getAttribute("data-id");
		selectedCategoryId = categoryId;
		try {
			const res = await fetch(`/api/words?categoryId=${categoryId}`);
			if (res.ok) {
				const words = await res.json();
				if (words.length === 0) {
					const msg = document.createElement("p");
					msg.textContent = "単語がありません";
					wordList.appendChild(msg);
					//カテゴリ削除ボタンを見える化
					deleteBtn.classList.replace("deleteBtnHidden", "deleteBtnVisible");
				}
				for (const word of words) {
					const li = document.createElement("li");
					const a = document.createElement("a");
					a.textContent = word.wordName;
					a.href = `/wordDetail/${word.id}`;
					li.appendChild(a);
					wordList.appendChild(li);
				}
			}
		} catch (error) {
			console.error(error);
			const msg = document.createElement("msg");
			msg.textContent = "取得に失敗しました";
			wordList.appendChild(msg);
		}
	});
});
// カテゴリ削除処理
document.querySelectorAll(".deleteBtnHidden").forEach(deleteBtn => {
	deleteBtn.addEventListener("click", async () => {
		try {
			const res = await fetch(`/api/categories/${selectedCategoryId}`, {
				method: "DELETE",
			});
			if (res.ok) {
				location.reload();
			} else {
				alert("削除に失敗しました");
			}
		} catch (error) {
			console.error(error);
			alert("通信エラーが発生しました");
		}
	});
});

