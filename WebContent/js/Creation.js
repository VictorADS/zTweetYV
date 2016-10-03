jQuery(document).ready(function(){
	console.log("JQuery Operationel");
	return false;
});

function applyform(form){
	return verif_form(form);
}
function verif_form(form){
	var mailre=/[^ @]+@[a-zA-Z0-9]+\.([a-z]+)/;
	var mdpre=/([^ \"\']){6,}/;
	var nom=form.Nom.value;
	var prenom=form.Prenom.value;
	var mail=form.Mail.value;
	var login=form.Login.value;
	var mdp=form.Mdp.value;
	var mdpverif=form.Mdpverif.value;
	var bool=true;
	if(nom.length==0 && prenom.length==0){
		adderror("Nom et prenom invalides",$("[name='Prenom']"),"nom");
		bool=false;
	}else{
		adderror("",$("[name='Prenom']"),"nom");

		if(nom.length==0){
			adderror("Nom invalide",$("[name='Prenom']"),"nom");
			bool=false;
		}
		if(prenom.length==0){
			adderror("Prenom invalide",$("[name='Prenom']"),"nom");
			bool=false;
		}
	}
	if(login.length==0){
		adderror("Login invalide",$("[name='Login']"),"login");
		bool=false;	
	}else{
		adderror("",$("[name='Login']"),"login");
	}
	if(!mailre.exec(mail)){
		adderror("Structure non valide",$("[name='Mail']"),"mail");
		bool=false;
	}else{
		adderror("",$("[name='Mail']"),"mail");
	}
	if(!mdpre.exec(mdp)){
		adderror("Doit contenir 6 caractère minimums",$("[name='Mdp']"),"mdp");	
		bool=false;
	}else{
		adderror("",$("[name='Mdp']"),"mdp");
	}
	if(mdpverif!=mdp){
		adderror("Mot de passes ne correspondent pas",$("[name='Mdpverif']"),"mdpverif");
		bool=false;
	}else{
		adderror("",$("[name='Mdpverif']"),"mdpverif");
	}
	return bool;
}
$(function(){	
	$("input").keyup(function(e){
		e.preventDefault();
		var input=this;
		var mailre=/[^ @]+@[a-zA-Z0-9]+\.([a-z]+)/;
		var mdpre=/([^ \"\']){6,}/;

		if(input.name=="Nom" || input.name=="Login" || input.name=="Prenom"){
			if(input.value.length!=0){
				changecss(input,"valid");
			}else{
				changecss(input,"invalid");
			}	
		}else{
			if(input.name=="Mail"){
				if(mailre.exec(input.value)){
					changecss(input,"valid");
				}else{
					changecss(input,"invalid");
				}
			}else{
				if(input.name=="Mdp"){
					if(mdpre.exec(input.value)){
						changecss(input,"valid");						
					}else{
						changecss(input,"invalid");
					}
					($("input[name='Mdpverif']").keyup());
				}else{
					if(input.name=="Mdpverif"){
						var mdp=$("input[name='Mdp']").get(0).value;
						if(mdp == input.value && input.value.length>= 6){
							changecss(input,"valid");						
						}else{
							changecss(input,"invalid");
						}
					}
				}
			}
		}
	});
});
function adderror(msg,input,labelname){
	var label=$("label[for='"+labelname+"']");
	var content = $("<Label for='"+labelname+"'>"+msg+"</Label>");
	$(content).css({
		color: 'red',
		'font-size' : '12px',
		'margin-left' : '5px'
	});
	if(label.length==0){
		input.after($(content));
	}else{
		label.text(msg);
	}
}
function changecss(input,isvalid){
	var cssproperty=$(input).css("background-image");
	var re=/images\/invalid/;
	var cssinvalid=re.exec(cssproperty);
	if(isvalid=="valid"){
		if(cssinvalid){
			console.log("valid");
			$(input).css("background-image","url('css/images/valid.png')");
		}
	}else{
		if(!cssinvalid){
			console.log("invalid");
			$(input).css("background-image","url('css/images/invalid.png')");
		}	
	}
}

$(function(){
	$('#formulaire').submit(function(event){
		event.preventDefault(); //Empêche le navigateur de suivre le lien.
		console.log("Inscription en cours...");
		form=this;
		
		if(verif_form(form)){
			var nom=form.Nom.value;
			var prenom=form.Prenom.value;
			var email=form.Mail.value;
			var login=form.Login.value;
			var mdp=form.Mdp.value;
			requeteAJAX(email,login,nom,prenom,mdp);
		}
	});
});

function requeteAJAX(email,login,nom,prenom,mdp){
	console.log("Envoi des données avec " + email);
	$.ajax({
		url : 'user/create',
		type : 'GET',
		data : 'email='+email+'&login='+login+"&pass="+mdp+"&nom="+nom+"&prenom="+prenom,
		contentType: 'application/json; charset=utf-8',
		dataType:'json',
		success : function(rep){
			console.log("succes");
			if(rep.Erreur==undefined ){
				changelabel("","erreurajax",$(".erreurajax"));
				console.log("Inscrit, Mail envoyé");
				window.location.href='main.html';
			}else{
				changelabel(rep.Erreur,"erreurajax",$(".erreurajax"));
				console.log(rep.Erreur);
			}
		},

		error: function(resultat, statut, erreur){
			console.log("res "+resultat+" stat "+statut+" err "+erreur);
		}
	});

};
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

