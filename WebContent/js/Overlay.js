function createOverLay(msg){
	console.log("YOOZAE");
	s="<div id=\'errorOverlay\'>\n";
	s+="<div class=\'errorcontain\'>\n";
	s+="<form id=\"form\" name=\"formulaire\" action=\"\">";
	s+="<div class=\'message\'>\n";
	s+="<p>"+msg+"</p></div>";
	s+="<div class=\"erreurajax\"></div>";
	s+="<div class=\"ids\">"+
	"<input type=\"text\" name=\"login\" placeholder=\"Login\"></div>"+
	"<div class=\"ids\">"+
	"<input type=\"password\" name=\"mdp\" placeholder=\"Mot de passe\"></div>"+
	"<div class=\"Connexion\">";
	s+="<input type=\"submit\" name=\"connexion\" value=\"Connexion\">";
	s+="</div></form>\n</div>\n</div>";
	$("#fixe").after($(s)).fadeIn('fast',function(){
		$('.errorcontain').animate({'top':'200px'},1000);
	});
	$("body").on("submit", "#form", function(event) {
		event.preventDefault();
		var login=$("[name='login']").val();
		var mdp=$("[name='mdp']").val();
		console.log(mdp+" et "+login+" et "+login.length);
		if(verif_form(login,mdp)){
			console.log("yo");
			requeteAJAX(login,mdp);
			return false;
		}else{
			return false;
		}
	});
}

function verif_form(login,mdp){
	var loginput=$(".ids ");
	var bool=true;
	if(mdp.length < 6){
		change_label("errormdp","Votre mot de passe est trop court.",loginput.get(1));
		bool=false;
		console.log("mdp short");
	}else{	
		change_label("errormdp","",null);
		console.log("mdpok");

	}
	if(login.length==0){
		change_label("errorlog","Veuillez entrez un login valide.",loginput.get(0));
		bool=false;
		console.log("log short");
	}else{
		var myre=/[\s\\\"]/;
		if(myre.exec(login)){
			change_label("errorlog","Caracteres speciaux detectes (\", ,\\).",loginput.get(0));
			bool=false;
			console.log("spec");

		}else{
			change_label("errorlog","",null);
			console.log("logok");

		}
	}
	console.log(""+bool);
	return bool;
}
function change_label(labelname,msg,here){
	var label=$("label[for='"+labelname+"']");
	var content = $("<Label for='"+labelname+"'>"+msg+"</Label>");
	$(content).css({
		color: 'red',
		'font-size' : '12px',
		'margin-left':'20px'
	});
	if(label.length==0){
		$(content).appendTo(here);
	}else{
		label.text(msg);
	}
}
function requeteAJAX(login,mdp){
	console.log("Connexion de "+login+" mdp "+mdp);
	$.ajax({
		url: 'user/log',
		type : 'GET',
		data : 'login='+login+"&pass="+mdp,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : function(rep){
			console.log(JSON.stringify(rep));
			if(rep.Erreur==undefined){
				var newpath=window.location.pathname.replace(/\/zTweetYV\//,"");
				var param=getGoodParameters(newpath,rep);
				console.log(param);
				window.location.href =newpath+"?"+param;
			}else{
				change_label("erreurajax","Verifiez votre login ou votre mot de passe",$(".erreurajax"));
				console.log("Connexion rate");
			}
		},
		error: function(resultat, statut, erreur){
			console.log("Bug");
			alert("dawg");
		}
	});

};
function isPathToAccueil(path){
	var test=/accueil/;
	return test.exec(path);
}
function isPathToSettings(path){
	var test=/Settings/;
	return test.exec(path);
}
function isPathToProfil(path){
	var test=/profile/;
	return test.exec(path);
}
function getGoodParameters(path,rep){
	console.log("Enter");
	if(isPathToAccueil(path)){
		return "id="+rep.id+"&login="+rep.login+"&key="+rep.key;
	}
	if(isPathToProfil(path)){
		return "loginAuteurCommentaire="+rep.login+"&keyUserSession="+rep.key;
	}
	if(isPathToSettings(path)){
		return "key="+rep.key;
	}
}