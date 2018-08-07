$(document).ready(function() {

    $('#uploadNewRelease').on('change', function(event) {
        $(this).next('.custom-file-label').addClass('selected').html($(this).val().split('\\').pop());
    });

	$('form').on('submit', function(event) {
		event.preventDefault();
		var formData = new FormData($('form')[0]);

		$.ajax({
			xhr : function() {
				var xhr = new window.XMLHttpRequest();
				xhr.upload.addEventListener('progress', function(e) {
					if (e.lengthComputable) {
						console.log('Bytes Loaded: ' + e.loaded);
						console.log('Total Size: ' + e.total);
						console.log('Percentage Uploaded: ' + (e.loaded / e.total))

						var percent = Math.round((e.loaded / e.total) * 100);

						$('#progressBar').attr('aria-valuenow', percent).css('width', percent + '%').text(percent + '%');
					}
				});
				return xhr;
			},
			type : 'POST',
			url : '/s3/uploadFile',
			data : formData,
			processData : false,
			contentType : false,
			success : function() {
				window.location.reload(true);
			}
		});

	});

});