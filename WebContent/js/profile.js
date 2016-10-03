function main(loginProfile,key ){	
	/* Stockage variable appel JSP*/
	if(key !=undefined){

		env=new Object();
		env.key=key;
		env.profil=loginProfile;
		ajaxRecupInfoUser(key);
	}else{
		initPage();
		ShowProfile("",loginProfile);
	}
}
function ajaxRecupInfoUser(key){
	$.ajax({
		url: 'settings/get',
		type : 'GET',
		data : 'key='+key,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : createUserActif,
		error: function(resultat, statut, erreur){
			alert("dawg");
		}
	});
}
function createUserActif(rep){
	if(rep.Erreur==undefined){
		env.actif=new User(rep.id,rep.login);
		initPage();
		ShowProfile(env.actif.login, env.profil);
	}else{
		var notconnected=/.*non connecte.*/;
		if(notconnected.exec(rep.Erreur)){
			alert("Vous n'etes plus connecte. En appuyant sur OK vous allez etre redirige vers la page de connexion");
			window.location.href="main.html";
		}
	}
}
function initPage(){
	if(env!=undefined && env.actif!=undefined){
		$("[name='profil']").text(env.actif.login);
	}else{
		$("[name='deco']").text("Connexion");
		$("[name='profil']").remove();
		$("[name='settings']").remove();
		$(".checkbox").remove();
	}
}
function ShowProfile(login,loginProfile){

	$.ajax({
		url: 'profil/idUser',
		type : 'GET',
		data : 'MyLogin='+login+"&OtherLogin="+loginProfile,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : Profile.prototype.traiteReponseJSON,
		error: function(resultat, statut, erreur){
			alert("YOLO");}
	});

};


Profile.prototype.traiteReponseJSON = function(json_txt) {
	json_txt=JSON.stringify(json_txt);
	console.log(json_txt);
	var RetourRevival = JSON.parse(json_txt, Profile.prototype.revival);
	if(RetourRevival.erreur == undefined){
		RetourRevival.getHtml();
	}else {
		console.log(RetourRevival.erreur);	
	}

};

Profile.prototype.revival = function(key, value) {
	//FIN DU JSON
	if(key.length == 0){
		var retour;
		//Si tout c'est bien passé ,on trouve pas un champs Erreur dans le JSON
		if((value.Erreur == undefined) || (value.Erreur == 0)){ 
			retour = new Profile( value.nbAmis, value.Amis, value.tweet, value.InfoProfil, value.nbMessages);
		} 
		else {
			retour = new Object();
			retour.erreur = value.Erreur;
		}

		return (retour); //->

		//RESTE DU JSON	
	}else if((isNumber(key)) && (value.auteur instanceof User)) { // Si l'on est dans une case du tableau et que l'auteur est un utilisateur
		var c = new Commentaire(value._id, value.auteur, value.texte, value.date, value.score);
		return c;

	}else if(key == "auteur") {
		var u;
		u = new User(value.idauteur,value.login,value.contact);
		return u;
	}
	else if(key == "InfoProfil") {
		var u;
		u = new InfoProfil(value.email,value.prenom,value.name,value.login, value.contact);
		return u;
	}
	else if(key == "date") {
		var d=new Date(value);
		return d;
	}
	else{// (nbAmis,(Commentaire:Date,id,texte,(Auteur:login,id,contact),Amis,nbMessages(Profil:nom,prenom,login,email))
		return (value);
	}
};

function isNumber(s){ //utilisé dans le revival
	return ! isNaN (s-0);
}
//constructeur Profil
function Profile(nbAmis, Amis, tweets, auteur, nbMessages) {
	this.nbAmis = nbAmis;
	this.Amis = Amis;
	this.tweets = tweets;
	this.auteur = auteur;
	this.nbMessages = nbMessages;
}
function InfoProfil(email,prenom,name,login,contact){
	this.prenom=prenom;
	this.email=email;
	this.name=name;
	this.login=login;
	this.contact=contact;	
}
//Constructeur user
function User(id,login,contact){
	this.id=id;
	this.login = login;
	this.contact=contact;
}
User.prototype.modifstatus=function(){
	this.contact=!this.contact;
};





Profile.prototype.getHtml = function(){
	//Ses Commentaires
	var s = "<div id = \"commcontainer\">\n";

	for(var i = 0; i < this.tweets.length; i++){
		s+= this.tweets[i].getHtml() + "\n";
	}
	s+= "</div>";

	AfficherCommentaires(s);
	s="";


	//Ses infos
	//Nombre de Messages
	s="<p>";
	s+="Nombre de messages: "+this.nbMessages;	
	s+="</p>";

	//Nombre d'amis
	s+="<p>";
	s+="Nombre D'amis: "+this.nbAmis;	
	s+="</p>";

	//noms de ses Amis avec liens
	s+="<p> Amis: ";
	for(var i = 0; i < this.Amis.length; i++){

		s+="<a href=\"profile.jsp?loginAuteurCommentaire="+this.Amis[i]+"";
		if(env!=undefined && env.actif!=undefined){
		s+="&keyUserSession="+env.key+"\"";
		}
		s+=">"+this.Amis[i]+"</a>";  
		//s+=this.Amis[i]+", ";
	}
	s+="</p>";
	AfficherStatitiques(s);
	/*
	for(var i = 0; i < this.tweets.length; i++) { //partie commentaire
		s+= this.tweets[i].getHtml() + "\n";
	}
	 */
	return (s);
};
//Fin constructeur
function AfficherCommentaires(text){
	$("#commcontainer").replaceWith(text);
}
function AfficherStatitiques(text){
	$("#Statistiques").append(text);
}

function changeCommentaire(resultat){
	var zonecommentaire=$("#commcontainer");
	$("#Statistiques").remove();
	if(zonecommentaire.length==0){
		$(".middle").append(resultat.getHtml());
	}else{
		zonecommentaire.replaceWith(resultat.getHtml());
	}
}



function TraiteReponseJSONMP(json_txt) {
	var old_users = env.users;
	env.users = [];	
	json_txt=JSON.stringify(json_txt);
	var obj = JSON.parse(json_txt, RechercheCommentaire.prototype.revival);
	if(obj.erreur == undefined){
		changeCommentaire(obj);
	}else {
		env.users = old_users;
		var nocomment=/Pas de commentaires/;
		if(nocomment.exec(obj.erreur)){
			$("#Statistiques").remove();
			$("#commcontainer").html("<div class=\"notfound\"><p> La recherche n'a abouti a aucun resultat </p></div>");

		}
		if(/Cle perime/.exec(obj.Erreur)){
			createOverLay("Veuillez vous reconnectez");
		}
	}

};
//-----
