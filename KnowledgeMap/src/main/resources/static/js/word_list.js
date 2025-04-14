const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordList = document.getElementById("wordList");

categoryBtns.forEach(categoryBtn => {
	categoryBtn.addEventListener("click", async () => {
		wordList.innerHTML = "";
		const categoryId = categoryBtn.getAttribute("data-id");
		try {
			const res = await fetch(`/api/words?categoryId=${categoryId}`);
			if (res.ok) {
				const words = await res.json();
				if (words.length === 0) {
					const msg = document.createElement("p");
					msg.textContent = "単語がありません";
					wordList.appendChild(msg);
				}
				for (const word of words) {
					const li = document.createElement("li");
					const a = document.createElement("a");
					a.textContent = word.wordName;
					a.href = `/wordDetail/${word.id}`; // ← ここでクエリパラメータ付与
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
