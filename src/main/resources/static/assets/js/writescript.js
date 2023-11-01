//file add
$(document).on('change','.filebox .upload-hidden',function(){
    if(window.FileReader){
        // 파일명 추출
        var filename = $(this)[0].files[0].name;
    }

    else {
        // Old IE 파일명 추출
        var filename = $(this).val().split('/').pop().split('\\').pop();
    };

    $(this).siblings('.upload-name').val(filename);
});

//preview image
/*
$(document).on('change', '.preview-image .upload-hidden',function(){
    var parent = $(this).parent();
    parent.children('.upload-display').remove();

    if(window.FileReader){
        //image 파일만
        if (!$(this)[0].files[0].type.match(/image\//)) return;

        var reader = new FileReader();
        reader.onload = function(e){
            var src = e.target.result;
            parent.prepend('<div class="upload-display"><div class="upload-thumb-wrap"><img src="'+src+'" class="upload-thumb"></div></div>');
        }
        reader.readAsDataURL($(this)[0].files[0]);
    }

    else {
        $(this)[0].select();
        $(this)[0].blur();
        var imgSrc = document.selection.createRange().text;
        parent.prepend('<div class="upload-display"><div class="upload-thumb-wrap"><img class="upload-thumb"></div></div>');

        var img = $(this).siblings('.upload-display').find('img');
        img[0].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enable='true',sizingMethod='scale',src=\""+imgSrc+"\")";
    }
});
*/

function removeItem() {
    //return this.parentNode.remove();
    let class_cnt = document.getElementsByClassName('filebox').length;

    let index = 1;
    let item = document.querySelectorAll(".filebox");

    item.forEach((ob)=>{
        ob.children[1].setAttribute("for", "input_file" + index);
        ob.children[2].setAttribute("id", "input_file" + index);
        index++;
        //console.log(ob.children[2]);
    })

}

function addfiles() {
    $(".filearea").append('<div class="filebox bs3-primary preview-image">\n' +
        '          <input class="upload-name" value="파일선택" disabled="disabled" style="width: 50%;">\n' +
        '          <label for="input_file">찾아보기</label>\n' +
        '          <input type="file" id="input_file" name="files" class="upload-hidden" required/>\n' +
        '          <label class="removebox" style="background-color: #ffffff; border-color: #dddddd; color: #000" onclick="this.parentNode.remove(); removeItem();">✖</label>\n' +
        '        </div>');

    let index = 1;
    let item = document.querySelectorAll(".filebox");

    item.forEach((ob)=>{
        ob.children[1].setAttribute("for", "input_file" + index);
        ob.children[2].setAttribute("id", "input_file" + index);
        index++;
        //console.log(ob.children[2]);
    })
};