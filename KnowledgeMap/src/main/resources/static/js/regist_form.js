const link = document.getElementById("existingWordLink");
const existingWordDetail = document.querySelector(".existingWordDetail");
const switcher = document.querySelector(".switcher");

link.addEventListener("click", async () => {
	console.log(switcher.textContent)
	existingWordDetail.classList.toggle("existingWordDetailVisible");
	if (!switcher.classList.toggle("bi-caret-down-fill")) {
		switcher.classList.add("bi-caret-up-fill");
	} else {
		switcher.classList.remove("bi-caret-up-fill");
	}
})