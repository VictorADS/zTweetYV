function Commentaire(id,auteur,texte,date,score){
	this.id=id;
	this.auteur=auteur;
	this.texte=texte;
	this.date=date;
	this.score=score;
}


Commentaire.prototype.getHtml=function(){
	var s="<div class='comm' value='"+this.id+"'>\n";
	s+="<div class=\'namedate\'>\n";
	var href="loginAuteurCommentaire="+this.auteur.login;
	if(env != undefined && env.actif !=undefined){
		href+="&keyUserSession="+env.key;//url pour profil
	}
	s+="<a href=\"profile.jsp?"+href+"\">Poste par "+this.auteur.login+"</a> <span>"+DatetoString(this.date)+"</span>\n</div>\n";
	s+="<div class=\'text\'>\n<p>"+this.texte+"</p>\n</div>\n";
	s+="<div class=\'buttonbottom\'>\n";
	if(env!=undefined && env.actif!=undefined && this.auteur.id!=env.actif.id){
		console.log("Here with "+this.auteur.id+" et "+env.actif.id);
		if(this.auteur.contact){
			console.log("oui amis");
			s+="<a class=\""+this.auteur.id+"\" name=\'supp\' onclick=\"ajaxFriendModif(\'"+this.auteur.id+"\',\'"+this.auteur.login+"\')\">\n<img src=\'css/images/Del.png\' alt=\'Icone del\' />\n</a>\n";		//ajouter icone - pour supprimer ami
		}else{
			console.log("pas amis");
			s+="<a class=\""+this.auteur.id+"\" name=\'add\' onclick=\"ajaxFriendModif(\'"+this.auteur.id+"\',\'"+this.auteur.login+"\')\" >\n<img src=\'css/images/Add.png\' alt=\'Icone ajouter\' />\n</a>\n";		//ajouter icone - pour supprimer ami		
		}

	}else{
		if(env!=undefined && env.actif !=undefined && this.auteur.id==env.actif.id){
			console.log("cest moi");

			s+="<a name=\'suppcom\' onclick=\"SuppCommentaire(\'"+this.id+"\')\">\n<img src=\'css/images/Delcom.png\' alt=\'Icone delCom\' />\n</a>\n";		//ajouter icone - pour supprimer ami
		}
	}
	console.log("TEXTE "+this.texte);
	s+="</div>\n"; //buttonbottom

	s+="</div>\n"; //comm
	return s;
};

function RechercheCommentaire(resultats, recherche, contacts_only, auteur, date) {
	this.resultats = resultats;
	this.auteur = auteur;
	this.recherche="";
	if(recherche != undefined) {
		this.recherche = recherche;
	}
	this.date = date;
	if(date == undefined) {
		this.date = new Date();
	}
	this.contact = contacts_only;
	if(contacts_only == undefined) {
		this.contact = false;
	}
	env.recherche = this;
}
function isNumber(s){
	return ! isNaN (s-0);
}
RechercheCommentaire.prototype.getHtml = function(){
	var s = "<div id = \"commcontainer\">\n";
	for(var i = 0; i < this.resultats.length; i++) {
		s+= this.resultats[i].getHtml() + "\n";
	}
	s+= "</div>";
	return (s);
};
RechercheCommentaire.prototype.revival= function(key, value) {
	if(key.length == 0) /* "haut" du JSON ==fin */
	{
		var r;
		if((value.Erreur == undefined) || (value.Erreur == 0)){ // Si l'on trouve pas un champs Erreur dans le JSON
			r = new RechercheCommentaire(value.resultats, value.recherche, value.contact, value.auteur, value.date);
		}
		else {
			r = new Object();
			r.Erreur = value.Erreur;
		}
		return (r);
	}
	else if((isNumber(key)) && (value.auteur instanceof User)) { // Si l'on est dans une case du tableau et que l'auteur est un utilisateur
		var c = new Commentaire(value._id, value.auteur, value.texte, value.date, value.score);

		return c;
	}
	else if(key == "date"){ // Si la clé  est une date
		var d=new Date(value);

		return (d);
	}
	else if(key == "auteur") { // Lorsquon doit crée un utilisateur
		var u;
		if((env != undefined) && (env.users != undefined) && (env.users[value.idauteur] != undefined)){	
			u = env.users[value.idauteur];
		}
		else {
			u = new User(value.idauteur, value.login, value.contact);
		}

		return u;
	}
	else{
		return (value);
	}
};


function ajaxFriendModif(idhref,login){
	var ahref=$("."+idhref);
	var url="";
	var name=ahref.attr("name");
	if(name=="add"){
		url="friend/add";
	}
	if(name=="supp"){
		url="friend/del";
	}
	$.ajax({
		url: url,
		type : 'GET',
		data : 'cle='+env.key+"&loginf="+login,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : function(rep){
			if(rep.Erreur ==undefined){
				console.log(idhref+"et "+login);
				if(name=="add"){
					ahref.replaceWith("<a class=\""+idhref+"\" name=\'supp\' onclick=\"ajaxFriendModif(\'"+idhref+"\',\'"+login+"\')\">\n<img src=\'css/images/Del.png\' alt=\'Icone del\' />\n</a>\n");

				}
				if(name=="supp"){
					ahref.replaceWith("<a class=\""+idhref+"\" name=\'add\' onclick=\"ajaxFriendModif(\'"+idhref+"\',\'"+login+"\')\">\n<img src=\'css/images/Add.png\' alt=\'Icone ajoute\' />\n</a>\n");
				}
			}
		},
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
function SuppCommentaire(id){
	var r = confirm("Vous etes sur le point de supprimer votre message.\n Appuyez sur OK pour confirmer.");
	if(r){
		$.ajax({
			url: "session/del",
			type : 'GET',
			data : 'cle='+env.key+"&idcom="+id,
			contentType: 'application/json; charset=utf-8',
			dataType:'json',
			success : function(rep){
				if(rep.Erreur ==undefined){
					$(".comm[value="+id+"]").toggle("slide");
				}else{
					if(/Cle perime/.exec(rep.Erreur)){
						createOverLay("Veuillez vous reconnectez");
					}
				}
			},
			error: function(resultat, statut, erreur){
				alert("dawg");
			}
		});
	}

}
$(function(){
	$('[name="accueil"]').click(function(event){
		if(env!=undefined && env.actif!=undefined){
			var u=env.actif;
			window.location.href ='accueil.jsp?id='+u.id+'&login='+u.login+'&key='+env.key;
		}else{
			console.log("bug you shouldnt be here");
		}
	});
});
$(function(){
	$('[name="settings"]').click(function(event){
		if(env!=undefined && env.actif!=undefined){
			console.log(env.key);
			console.log("id "+env.actif.id+" login "+env.actif.login);
			window.location.href='Settings.jsp?id='+env.actif.id+'&login='+env.actif.login+'&key='+env.key;
		}
	});
});
$(function(){
	$('[name="deco"]').click(function(event){
		event.preventDefault();//OnClick Deconnexion Connexion
		if(this.text=="Connexion"){
			console.log("yo");
			window.location.href='main.html';
		}else{
			console.log("yep");
			$.ajax({
				url: 'user/logout',
				type : 'GET',
				data : 'cle='+env.key,
				contentType: 'application/json; charset=utf-8',
				dataType:'json',
				success : Deconnexion,
				error: function(resultat, statut, erreur){
					alert("dawg");
				}
			});
		}
	});
});

function Deconnexion(rep){		//Function pour deconnecter lutilisateur
	if(rep.Erreur==undefined || /Cle perime/.exec(rep.Erreur)){
		env.actif=undefined;
		env.key=undefined;
		window.location.href='accueil.jsp';
	}
}
$(function(){
	$('[name="profil"]').click(function(event){
		var login=env.actif.login;
		window.location.href="profile.jsp?loginAuteurCommentaire="+login+"&keyUserSession="+env.key;
	});
});
$(function(){
	$('[name="accueil"]').click(function(event){
		var u=env.actif;
		window.location.href="accueil.jsp?login="+u.login+"&id="+u.id+"&key="+env.key;
	});
});

$(function(){
	$('[name="searchicon"]').click(function(event){
		event.preventDefault();
		var query=$('[name="searchbar"]').val();
		var friendsornot=0;
		var key="";
		if(env !=undefined && env.actif !=undefined){
			friendsornot=$("input:checked").length;
			key=env.key;
		}
		console.log("recherche de "+query+" with "+friendsornot+" avec "+key);
		$.ajax({
			url: 'search',
			type : 'GET',
			data : "key="+key+"&friends="+friendsornot+"&query="+query,
			contentType: 'application/json; charset=utf-8',
			dataType:'json',
			success : TraiteReponseJSONMP,
			error: function(resultat, statut, erreur){
				alert("dawg");
			}

		});
	});
});
function DatetoString(date){
	var dd = date.getDate();
	var mm = date.getMonth()+1;
	var yyyy = date.getFullYear();
	var now=new Date();
	if(now.getDate()== dd && now.getMonth()+1==mm && now.getFullYear() == yyyy){
		var hours=now.getHours()-date.getHours();
		var min=now.getMinutes()-date.getMinutes();	
		var time="Il y a "+hours+"h";
		if(hours==0){
			time="Il y a "+min+" min";
		}
		return time;
	}
	if(dd<10){
		dd='0'+dd;
	} 
	if(mm<10){
		mm='0'+mm;
	} 
	var today ="Le "+ dd+'/'+mm+'/'+yyyy;
	return today;
}
$(function(){
	$("[name='searchbar']").keypress(function(event) {
		console.log("yo");
	    if (event.which == 13) {
	    	console.log("yoz");
	        event.preventDefault();
		$('[name="searchicon"]').click();
	        return false;
	    }
	});
	});
$(document).ready(function(){
    // Condition d'affichage du bouton
    $(window).scroll(function(){
        if ($(this).scrollTop() > 100){
            $('.go_top').fadeIn();
        }
        else{
            $('.go_top').fadeOut();
        }
    });
    // Evenement au clic
    $('.go_top').click(function(){
        $('html, body').animate({scrollTop : 0},800);
        return false;
    });
});
