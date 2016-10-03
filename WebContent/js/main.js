
jQuery(document).ready(function(){
	console.log("JQuery Operationel");
	return false;
});
$(function(){
	$('#form').submit(function(event){
		event.preventDefault(); //Empêche le navigateur de suivre le lien.
		var mdp=form.mdp.value;
		var login=form.login.value;
		if(verif_form(login,mdp)){
			requeteAJAX(login,mdp);
		}else{
			return false;
		}

	});
});
function verif_form(login,mdp){
	var loginput=$(".ids ");
	var bool=true;
	if(mdp.length < 6){
		change_label("errormdp","Votre mot de passe est trop court.",loginput.get(1));
		bool=false;
	}else{		
		change_label("errormdp","",null);
	}
	if(login.length==0){
		change_label("errorlog","Veuillez entrez un login valide.",loginput.get(0));
		bool=false;
	}else{
		var myre=/[\s\\\"]/;
		if(myre.exec(login)){
			change_label("errorlog","Caracteres speciaux detectes (\", ,\\).",loginput.get(0));
			bool=false;
		}else{
			change_label("errorlog","",null);
		}
	}
	return bool;
}
function change_label(labelname,msg,here){
	var label=$("label[for='"+labelname+"']");
	var content = $("<Label for='"+labelname+"'>"+msg+"</Label>");
	$(content).css({
		color: 'red',
		'font-size' : '12px',
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
				console.log("Connexion reussi");
				window.location.href ='accueil.jsp?id='+rep.id+'&login='+rep.login+'&key='+rep.key;
			}else{
				change_label("erreurajax","Vérifiez votre login ou votre mot de passe",$(".erreurajax"));
				console.log("Connexion rate");
			}
		},
		error: function(resultat, statut, erreur){
			console.log("Bug");
			alert("dawg");
		}
	});

};
