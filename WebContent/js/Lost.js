
jQuery(document).ready(function(){
	console.log("JQuery Operationel");
	return false;
});

 
//Verifier la regex (Tools)
function verifIsMail(mail){
	if (/^[a-z0-9._-]+@[a-z0-9._-]+\.[a-z]{2,6}$/.test(mail)){
		return true;
	}else{
		return false;
		}
}
function changelabel(msg,labelname,here){
	var label=$("label[for='"+labelname+"']");
	var content = $("<Label for='"+labelname+"'>"+msg+"</Label>");
	$(content).css({ 
    	color : 'red',
		'font-size' : '12px',
    });
	if(label.length==0){
		$(content).appendTo(here);
	}else{
		label.text(msg);
	}

}
//notifier à  l'utilisateur les erreurs 
$(function(){
	$('#lemail').keyup(function(){
		var mail = $(this).val();
	    if(!verifIsMail(mail)){ 
	    	changelabel("Mail incorrect","erreurmail",$(".mail"));
	    }
	    else{
	    	changelabel("","erreurmail",$(".mail"));
	    }
	});
});

//envoyer le formulaire et la requete correspodante
$(function(){
	$('#formulaire').submit(function(event){
		event.preventDefault(); //Empêche le navigateur de suivre le lien.

		var mail=$('#lemail').val();
		//verifIsMail(form.Mail.value) // ceci ne marche pas
		if (verifIsMail(mail)){
			console.log("recuperation du compte de " + mail);
			requeteAJAX(mail);
		}else{
			console.log("syntaxe du mail erronée");
			return false;
		}
	});
});


function requeteAJAX(email){
	console.log("Envoi des données avec " + email);
    $.ajax({
    	url : 'profil/forgot',
       type : 'GET',
       data : 'mail='+email,
       contentType: 'application/json; charset=utf-8',
       dataType:'json',
       success : function(rep){
    	   if(rep.Erreur ==undefined ){
   	    	changelabel("","erreurajax",$(".erreurajax"));
    	   console.log("Mail envoyer");
    	   }else{
   	    	changelabel("Ce compte mail n'existe pas","erreurajax",$(".erreurajax"));
   	    	console.log(rep.Erreur);
   	    	
    	   }
       },

       error: function(resultat, statut, erreur){
         	console.log("res "+resultat+" stat "+statut+" err "+erreur);
       }
    });
   
};
