function main(key){	
	env = new Object();
	if (key != undefined){
		ajaxRecupParam(key);
	}else{
		console.log("Acces non autorise ");
		window.location.href="accueil.jsp";
	}
}

function User(id,login,contact,mail,key){
	console.log("Id "+id+" Log "+login+" contact" +contact+" mail"+mail+" key "+key);
	this.id=id;
	this.login = login;
	if(env==undefined){
		env=new Object();
	}
	if(env!=undefined && key!=undefined && env.actif==undefined){
		env.key=key;
	}
	console.log(key+" et "+env.key);
	this.mail=mail;
	this.contact=false;
	if(contact!=undefined)
		this.contact=contact;
}
function ajaxRecupParam(key){
	$.ajax({
		url: 'settings/get',
		type : 'GET',
		data : 'key='+key,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : createUser,
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
function createUser(rep){
	if(rep.Erreur==undefined){
		env.actif=new User(rep.id,rep.login,false,rep.mail,rep.key);
		initPage();
	}else{
		console.log(rep.Erreur);
		var notconnected=/.*non connecte.*/;
		var perime=/.*Cle perime.*/;
		if(notconnected.exec(rep.Erreur)){
			alert("Vous n'etes plus connectez. En appuyant sur OK vous allez etre redirige vers la page de connexion");
			window.location.href="main.html";
		}
		if(perime.exec(rep.Erreur)){
			createOverLay("Votre session a expire veuillez vous reconnecter");
		}
	}
}


function initPage(){
	$("[name='profil']").text(env.actif.login);
	$("[name='Login']").val(env.actif.login);
	$("[name='mail']").val(env.actif.mail);
	$("[name='pwd']").val("");
}
$(function(){
	$("[name='Login']").keyup(function(event){ 
		if(this.value.length==0){
			adderror("Login trop court",this,"loginerror");
		}
		if(this.value==env.actif.login){
			adderror("",this,"loginerror");
		}
		if(this.value!=env.actif.login && this.value.length!=0){
			ajaxVerifLogin(this.value,this);
		}
	});
});
$(function(){
	$("[name='mail']").keyup(function(event){ 
		var mailre=/[^ @]+@[a-zA-Z0-9]+\.([a-z]{2,})/;
		if(mailre.exec(this.value)){
			adderror("",this,"mailerror");
			ajaxVerifMail(this.value,this);
		}else{
			adderror("Mail non valide",this,"mailerror");
		}
	});
});
$(function(){
	$("[name='pwd']").keyup(function(event){ 
		var mdpre=/([^ \"\']){6,}/;
		if(!mdpre.exec(this.value)){
			adderror("Mot de passe trop court",this,"mdperror");
		}else{
			adderror("",this,"mdperror");

		}
	});
});
$(function(){
	$("[name='del']").click(function(event){
		event.preventDefault();
		var r = confirm("Vous etes sur le point de supprimer votre compte.\n Appuyez sur OK pour confirmer.");
		if(r){
			$.ajax({
				url: 'end',
				type : 'GET',
				data : 'login='+env.actif.login,
				contentType: 'application/json; charset=utf-8',
				dataType:'json',
				success : function(rep){
					if(rep.Erreur==undefined){
						alert("Aurevoir et a bientot");
						window.location.href="main.html";
					}else{
						console.log(rep.Erreur);

					}
				},
				error: function(resultat, statut, erreur){
					alert("dawg");
				}
			});
		}
	});
});
function ModifParamSuccess(){
	var s="<div class=\'succes\'>\n"
		+"<h4> Modifications reussies <span class=\"icone\"></span</h4>"
		+"</div>";
	$(".Annonce").after($(s));
	$("body").on("click", ".icone", function(event) {
		event.preventDefault();
		console.log("yo");
		$(".succes").remove();
	});
}


$(function(){
	$("#formulaire").submit(function(event){
		event.preventDefault();
		var mailinput=$("[name='mail']");
		var loginput=$("[name='Login']");
		var mdpinput=$("[name='pwd']");
		if(loginput.val() != env.actif.login || mailinput.val() != env.actif.mail || mdpinput.val().length>=6 ){
			$.ajax({
				url: 'settings/change',
				type : 'GET',
				data : 'login='+loginput.val()+"&mail="+mailinput.val()+"&mdp="+mdpinput.val()+"&user="+env.actif.login,//TODO add key
				contentType: 'application/json; charset=utf-8',
				dataType:'json',
				success : function(rep){
					if(rep.Erreur==undefined){
						var u=new User(env.actif.id,loginput.val(),false,mailinput.val(),env.key);
						env.actif=u;
						$("[name='profil']").text(env.actif.login);
						$("[name='Login']").val(env.actif.login);//TODO rajouter qq chose qui montre reussi
						ModifParamSuccess();
					}else{
						console.log(rep.Erreur);

					}
				},
				error: function(resultat, statut, erreur){
					alert("dawg");
				}
			});
		}
	});
});;
function ajaxVerifLogin(login,input){
	$.ajax({
		url: 'settings/change',
		type : 'GET',
		data : 'login='+login,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : function(rep){
			if(rep.Erreur==undefined){
				adderror("",input,"loginerror");
			}else{
				adderror("Login existe deja",input,"loginerror");
			}
		},
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
function ajaxVerifMail(mail,input){
	$.ajax({
		url: 'settings/change',
		type : 'GET',
		data : 'mail='+mail,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : function(rep){
			if(rep.Erreur==undefined){
				adderror("",input,"mailerror");
			}else{
				adderror("Mail existe deja",input,"mailerror");
			}
		},
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
function adderror(msg,input,labelname){
	var label=$("label[for='"+labelname+"']");
	var content = $("<Label for='"+labelname+"'>"+msg+"</Label>");
	$(content).css({
		color: 'red',
		'font-size' : '12px',
		'margin-left' : '5px'
	});
	if(label.length==0){
		$(input).after($(content));
	}else{
		label.text(msg);
	}
}
function TraiteReponseJSONMP(json_txt) {
	var old_users = env.users;
	env.users = [];	
	json_txt=JSON.stringify(json_txt);
	console.log(json_txt);

	var obj = JSON.parse(json_txt, RechercheCommentaire.prototype.revival);
	if(obj.erreur == undefined){
		changeCommentaire(obj);
	}else {
		env.users = old_users;
		var nocomment=/Pas de commentaires/;
		if(nocomment.exec(obj.erreur)){
			$("#changeparam").html("<div class=\"notfound\"><p> La recherche n'a abouti a aucun resultat </p></div>");
			$("#commcontainer").html("<div class=\"notfound\"><p> La recherche n'a abouti a aucun resultat </p></div>");
		}
		if(/Cle perime/.exec(obj.Erreur)){
			createOverLay("Veuillez vous reconnectez");
		}
	}

};
function changeCommentaire(resultat){
	$("#changeparam").remove();
	var zonecommentaire=$("#commcontainer");;
	if(zonecommentaire.length==0){
		$(".middle").append(resultat.getHtml());
	}else{
		zonecommentaire.replaceWith(resultat.getHtml());
	}
}
//---------------



