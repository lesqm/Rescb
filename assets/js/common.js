var ajaxSendBuffer = function (url, buffer, fileName, fileSize, start, end, uploadId, done, error) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('X-Upload-Size', fileSize + "");
    xhr.setRequestHeader('X-Upload-Start', start + "");
    xhr.setRequestHeader('X-Upload-End', end + "");
    xhr.setRequestHeader('X-Upload-Name', encodeURIComponent(fileName));
    if (uploadId !== "")
        xhr.setRequestHeader('X-Upload-Id', uploadId);
    //xhr.setRequestHeader('Content-Type', 'application/octet-stream');

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                done(JSON.parse(xhr.responseText));
            } else {
                error();
            }
        }
    };

    xhr.send(buffer);
};

function ajaxPartUploadInt(url, chunkNo, file, uploadId, progress, done, error) {
    var chunkMaxSize = 1024 * 256;//1024 * 512;
    var chunkCount = Math.ceil(file.size / chunkMaxSize);
    var start = chunkNo * chunkMaxSize;
    var end = (chunkNo + 1) * chunkMaxSize;
    if ((chunkNo + 1) === chunkCount || chunkCount === 0) {
        end = file.size;
    }
    var blob = file.slice(start, end);
    var reader = new FileReader();

    reader.onloadend = function (evt) {
        if (evt.target.readyState === FileReader.DONE) {
            if (chunkNo < chunkCount) {
                ajaxSendBuffer(url, reader.result, file.name, file.size, start, end, uploadId,
                        function (data) {
                            console.log(data);
                            if (data.continue === false) {
                                progress(100);
                                done(data.uploadId);
                                return;
                            } else {
                                progress((chunkNo * 100) / chunkCount, undefined);
                                ajaxPartUploadInt(url, chunkNo + 1, file, data.uploadId, progress, done, error);
                            }
                        }, function () {
                    error();
                    return;
                });
            }
        }
    };

    reader.readAsArrayBuffer(blob);
}
;

var ajaxPartUpload = function (url, file, progress, done, error) {
    ajaxPartUploadInt(url, 0, file, '', progress, done, error);
};

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$(document).ready(function () {
    $("form.ajax-form").each(function () {
        var form = $(this);
        var button = form.find("button[type='submit']");
        var processbox = $(".processbox");
        var action = form.attr('action');

        if (action === undefined || action === false) {
            action = window.location.pathname;
        }

        var errorMessage = form.data("error");
        if (errorMessage === undefined || errorMessage === false) {
            errorMessage = "Error";
        }
        
        form.submit(function (e) {
            e.preventDefault();

            button.prop('disabled', true);
            processbox.fadeIn(200);

            var formData = form.serializeObject();
            var formFiles = form.find("input[type='file']");

            var errorFunc = function (data) {
                processbox.fadeOut(200);

                bootbox.alert(errorMessage);
                $("form.ajax-form button[type='submit']").prop('disabled', false);
            };

            var successFunc = function (data) {
                window.location.replace(data.url);
                if (window.location.href === data.url)
                    location.reload();
            };

            var ajaxFunc = function () {
                console.log(formData);
                $.ajax({
                    url: action,
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(formData),
                    success: function (data, msg) {
                        console.log(data);
                        if (data.status === "ok") {
                            successFunc(data);
                        } else {
                            errorFunc(data);
                        }
                    },
                    error: function (jqXHR, msg, throws) {
                        console.log(jqXHR);
                        console.log(msg);
                        console.log(throws);
                        errorFunc();
                    }
                });
            };


            if (formFiles.get(0) === undefined || formFiles.get(0).files[0] === undefined) {
                ajaxFunc();

            } else {
                ajaxPartUpload("/ajax/upload", formFiles.get(0).files[0],
                        function (percent) {
                            console.log(percent);
                            //button.text(Math.floor(percent) + "%");
                        },
                        function (uploadId) {
                            //button.text("Done!");
                            formData.fileId = uploadId;
                            ajaxFunc();
                        },
                        function () {
                            errorFunc("Some error accured");
                        });
            }


            return false;
        });
    });
});