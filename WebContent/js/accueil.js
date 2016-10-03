function main(id, login, key){	
	/* Stockage variable appel JSP*/
	env = new Object();
	env.users = [];

	if ( (id != undefined) && (login != undefined) && (key != undefined)){
		env.actif = new User(id, login);
		env.key = key;
		env.users[id]=env.actif;
		$("[name='profil']").text(login);
	}else{
		$("[name='deco']").text("Connexion");
		$("[name='profil']").remove();
		$("#formulaire").remove();
		$("[name='settings']").remove();
		$(".checkbox").remove();

	}
	SearchForTweet();
}
//Constructeur user
function User(id, login,contact){
	this.id = id;
	this.login = login;
	if(contact!=undefined){
		this.contact=contact;
	}
	if(env==undefined){
		env=new Object();
	}
	if(env.user==undefined){
		env.users=[];
	}
	env.users[this.id]=this;
}
User.prototype.modifstatus=function(){
	this.contact=!this.contact;
};

function changeCommentaire(resultat){
	var zonecommentaire=$("#commcontainer");

	if(zonecommentaire.length==0){
		$(".middle").append(resultat.getHtml());
	}else{
		zonecommentaire.replaceWith(resultat.getHtml());
	}
}
function createOverLayError(msg){
	s="<div id=\'errortweet\'>\n";
	s+="<div class=\'errorcontain\'>\n";
	s+="<div class=\'hide\'>\n <a name=\'hidebutton\' onclick=\'hideerror()\'>\n" +
	"<img src=\'css/images/invalid.png\' alt=\'croix\' />\n</a>\n</div>";
	s+="<div class=\'message\'>\n";
	s+="<p>"+msg+"</p>";
	s+="</div>\n</div>\n</div>";
	if($("#errortweet").length==0){
	$("#fixe").after($(s));
	}else{
		$(".message > p").text(msg);
	}
}

$(function(){
	$("[name='writecomment']").keypress(function(event) {
		console.log("yo");
	    if (event.which == 13) {
	    	console.log("yoz");
	        event.preventDefault();
	        $("#formulaire").submit();
	        return false;
	    }
	});
	});
$(function(){
	$('#formulaire').submit(function(event){		//Submit formulaire poster message
		event.preventDefault();
		var input=$("[name='writecomment']");
		if(/^\s*$/.exec(input.val())){
			createOverLayError("Il faut au moins un caractere.");
			return false;
		}
		if(input.val().length<=140){
			$.ajax({
				url: 'session/add',
				type : 'GET',
				data : 'cle='+env.key+"&text="+input.val(),
				contentType: 'application/json; charset=utf-8',
				dataType:'json',
				success : function AddCommentaire(rep){		//Log pour savoir si le commentaire a bien été ajouté
					if(rep.Erreur==undefined){
						if(env!=undefined && env.actif !=undefined){
							var newcomm=new Commentaire(rep.idobject,env.actif,input.val(),new Date());
							$(newcomm.getHtml()).hide().prependTo("#commcontainer").fadeIn(1000);
							$("label[for='numchar']").text("0/140 Caracteres").css('color','black');
							$("[name='writecomment']").val("");
						}
					}else{
						var alreadysent=/.*deja envoye.*/;
						if(alreadysent.exec(rep.Erreur)){
							createOverLayError("Vous avez deja envoye ce Tweet.");
						}
						if(/Session expire/.exec(rep.Erreur)){
							createOverLay("Session expiré, veuillez vous reconnectez.");
						}
						if(/Utilisateur deconnecte/.exec(rep.Erreur)){
							console.log("Tu ne devrais pas pouvoir ajoute de commentaire en etant deconencte");
						}

					}
				},
				error: function(resultat, statut, erreur){
					alert("dawg");
				}
			});
		}
	});
});
function hideerror(){
	var error=$("#errortweet");
	if(error.length!=0){
		error.remove();
	}
}
$(function(){
	$("[name='writecomment']").keyup(function(event){ //Change la couleur de numchar
		var input=this;
		var taille=input.value.length;
		var label=$("label[for='numchar']");
		var button=$("[name='postbutton']");
		if(taille>=141){
			label.css(({
				color: 'red',
			}));
			button.css(({
				'pointer-events':'none',
				opacity:0.4
			}));
		}else{
			label.css(({
				color: 'green',
			}));
			button.css(({
				'pointer-events':'all',
				opacity:1.0
			}));

		}
		label.text(taille+"/140 Caracteres");

	});
});


function TraiteReponseJSONMP(json_txt) {
	var old_users = env.users;
	env.users = [];	
	json_txt=JSON.stringify(json_txt);
	console.log(json_txt);

	var obj = JSON.parse(json_txt, RechercheCommentaire.prototype.revival);
	if(obj.Erreur == undefined){
		changeCommentaire(obj);
	}else {
		env.users = old_users;
		var nocomment=/Pas de commentaires/;
		if(nocomment.exec(obj.Erreur)){
			$("#commcontainer").html("<div class=\"notfound\"><p> La recherche n'a abouti a aucun resultat </p></div>");
		}
		if(/Cle perime/.exec(obj.Erreur)){
			createOverLay("Veuillez vous reconnectez");
		}
	}
	$("#formulaire").remove();

};
function SearchForTweet(){
	var key="";
	if(env!= undefined){
		if(env.key!=undefined){
			key=env.key;
		}

	}
	$.ajax({
		url: 'search',
		type : 'GET',
		data : 'key='+key+"&friends="+0,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : traiteReponseJSON,
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
	

function traiteReponseJSON(json_txt) {
	var old_users = env.users;
	env.users = [];	
	json_txt=JSON.stringify(json_txt);
	console.log(json_txt);
	var obj = JSON.parse(json_txt, RechercheCommentaire.prototype.revival);
	if(obj.Erreur == undefined){
		changeCommentaire(obj);
	}else {
		env.users = old_users;	
		if(/Cle perime/.exec(obj.Erreur)){
			createOverLay("Veuillez vous reconnectez");
		}
	}
};
//-------------------