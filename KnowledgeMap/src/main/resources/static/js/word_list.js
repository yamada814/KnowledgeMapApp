const categoryBtns = document.querySelectorAll(".categoryBtn");
const wordList = document.getElementById("wordList");
console.log(categoryBtns);

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
					const deleteBtn = document.querySelectorAll(".deleteBtn");
				}
				for (const word of words) {
					const li = document.createElement("li");
					li.textContent = word.wordName;
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
