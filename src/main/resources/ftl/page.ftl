(function(){
    var body = '';
    for (var i = 0; i <= ${pageSize}; i = i + ${increment}) {
        var url = '${format}';
        if(i == 0){
            url = url.replace(/\(.*\)/,'');
        }else{
            url = url.replace(/\(/,'').replace(/\)/,'').replace('${r'${index}'}',i);
        }
        for (var j = 0; j < 3; j = j + 1) {
            var success = false;
            $.ajax({
                url: url,
                async: false,
                success: function (data) {
                    var tagTpl = document.createElement('div');
                    tagTpl.innerHTML = data;
                    var content = ${body};
                    body += content;
                    success = true;
                }
            });
            if (success) {
                break;
            }
        }
    }
    return body;
}())
