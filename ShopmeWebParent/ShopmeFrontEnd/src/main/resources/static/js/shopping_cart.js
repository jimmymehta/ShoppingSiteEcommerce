decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

$(document).ready(function() {

	$(".linkMinus").on("click", function(evt) {

		evt.preventDefault();
		decreaseQuantity($(this));

	});

	$(".linkPlus").on("click", function(evt) {

		evt.preventDefault();
		increaseQuantity($(this));

	});

	$(".linkRemove").on("click", function(evt) {
		evt.preventDefault();
		removeProduct($(this));

	});


});

function decreaseQuantity(link) {

	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;

	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Minimum quantity should be 1');
	}
}

function increaseQuantity(link) {

	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;

	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Maximum quantity should be 5');
	}

}

function updateQuantity(productId, quantity) {

	quantity = $("#quantity" + productId).val();
	url = contextPath + "cart/update/" + productId + "/" + quantity;

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		updateEstimatedTotal();
	}).fail(function() {
		showErrorModal("Error while updating product quantity.");
	});

}


function updateSubtotal(updatedSubtotal, productId) {
	formattedSubtotal = $.number(updatedSubtotal, 2);
	$("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function updateEstimatedTotal() {
	total = 0.0;
	productCount = 0;

	$(".subtotal").each(function(index, element) {
		//alert(parseFloat(element.innerHTML));
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});

	if (productCount < 1) {
		showEmptyShoppingCart();
	} else {
		$("#total").text(formatCurrency(total));
	}
}

function removeProduct(link) {
	url = link.attr("href");

	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		rowNumber = link.attr("rowNumber");
		removeProductHTML(rowNumber);
		updateEstimatedTotal();
		updateCountNumbers();
		showModalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while removing product.");
	});
}

function removeProductHTML(rowNumber) {

	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();

}

function updateCountNumbers() {

	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function showEmptyShoppingCart() {
	$("#sectionTotal").hide();
	$("#sectionEmptyCartMessage").removeClass("d-none");
}

function formatCurrency(amount) {
	return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function clearCurrencyFormat(numberString) {
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
}