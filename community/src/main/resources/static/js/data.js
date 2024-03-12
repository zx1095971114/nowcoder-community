$(function (){
    $("#calculateUVBtn").click(calculateUV);
    $("#calculateDAUBtn").click(calculateDAU);
});

function calculateUV(){
    let startDate = $("#uvStartTime").val();
    let endDate = $("#uvEndTime").val();
    if(startDate == ""){
        alert("请选择要统计的网站uv的起始时间！");
        return;
    }else if(endDate == "") {
        alert("请选择要统计的网站uv的终止时间！");
        return;
    }

    startDate = new Date(startDate);
    endDate = new Date(endDate);

    //格式化
    let start = moment(startDate);
    start = start.format("yyyy-MM-DD");
    let end = moment(endDate);
    end = end.format("yyyy-MM-DD");

    if(startDate == null){
        alert("请选择要统计的网站uv的起始时间！");
    }else if(endDate == null){
        alert("请选择要统计的网站uv的终止时间！");
    }else if(startDate > endDate){
        alert("请保证uv起始时间小于终止时间！")
    } else {
        $.post(
            CONTEXT_PATH + "/data/calculateUV",
            {start: start, end: end},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    alert(data.msg);
                    $("#uvCount").text(data.uvCount);
                }else {
                    alert(data.msg);
                }
            }
        );
    }
}

function calculateDAU(){
    let startDate = $("#dauStartTime").val();
    let endDate = $("#dauEndTime").val();
    if(startDate == ""){
        alert("请选择要统计的网站dau的起始时间！");
        return;
    }else if(endDate == "") {
        alert("请选择要统计的网站dau的终止时间！");
        return;
    }

    startDate = new Date(startDate);
    endDate = new Date(endDate);

    //格式化
    let start = moment(startDate);
    start = start.format("yyyy-MM-DD");
    let end = moment(endDate);
    end = end.format("yyyy-MM-DD");

    if(startDate == null){
        alert("请选择要统计的网站dau的起始时间！");
    }else if(endDate == null){
        alert("请选择要统计的网站dau的终止时间！");
    }else if(startDate > endDate){
        alert("请保证dau起始时间小于终止时间！")
    } else {
        $.post(
            CONTEXT_PATH + "/data/calculateDAU",
            {start: start, end: end},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    alert(data.msg);
                    $("#dauCount").text(data.dauCount);
                }else {
                    alert(data.msg);
                }
            }
        );
    }
}