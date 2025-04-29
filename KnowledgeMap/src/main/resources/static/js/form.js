const categorySelect = document.getElementById("categoryId");//既存カテゴリの選択
const categoryInput = document.getElementById("categoryName");//新規カテゴリの入力
//新規カテゴリの入力があれば、既存カテゴリの選択はできない状態にする
categoryInput.addEventListener("input", () => {
	if (categoryInput.value !== "") {
		categorySelect.disabled = true;
	} else {
		categorySelect.disabled = false;
	}
});
$('#categoryId').select2({
  placeholder: "選択してください",
  allowClear: true
});
$('#relatedWordIds').select2({
  multiple: true,
  placeholder: "選択してください",
  allowClear: true
});
