<!DOCTYPE html >
<HTML>
<title>Param&egravetres</title>
<HEAD>
<meta charset="utf-8" />
<!--Lien vers la mise en page CSS-->
<link href="css/Settings.css" rel="stylesheet" TYPE="text/css">
<link href="css/Comments.css" rel="stylesheet" TYPE="text/css">
<link href="css/Overlay.css" rel="stylesheet" TYPE="text/css">

<meta name="viewport" content="width=device-width, initial-scale=1">
<!--Lien vers JQuerry-->
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js"></script>
<!--Lien vers le javaScript-->
<script type="text/javascript" src="js/Settings.js"></script>
<script type="text/javascript" src="js/Overlay.js"></script>
<script type="text/javascript" src="js/Util.js"></script>

</HEAD>
<BODY>
	<script type="text/javascript">
		function go() {
			<%
			String key = request.getParameter("key");
			if (key != null) {
				out.println("main('"+ key + "')");
			} else {
				out.println("main()");
			}%>
		}
		$(go);
	</script>
	<div id="wrapper">
		<div id="fixe">
			<div class="navbar">
				<div id="logo">
					<a class="nothightlight" name='accueil'>Accueil</a>
 					<a class="tohighlight" name='settings'>Param&egravetres</a>
				</div>
				<div class="spanleft">
					<ul>
						<li><input type="search" name="searchbar"
							placeholder="Entrez votre recherche ici"></li>
						<li><a name="searchicon"> </a></li>
					</ul>
				</div>
				<div class="checkbox">
					<input type="checkbox" name="friends" value="check">Friends
					only
				</div>
				<div class="spanright">
					<ul>

						<li><a name="profil"></a></li>

						<li><a name="deco">D&eacuteconnexion</a></li>
					</ul>
				</div>
			</div>
		</div>
		<div id="content">
			<div class="middle">

				<div id="changeparam">
					<div class="Annonce">
					<p> Sur cette page vous pouvez modifier les informations de votre compte </p>
					</div>
					
					<form id="formulaire" name="modification" action="">
						<div class="change">	

							<p>Nom utilisateur</p>
							<input type="text" name="Login" maxlength="20" placeholder="Login"><br>
							<p>Adresse email</p>
							<input type="text" name="mail" maxlength="40" placeholder="Email"><br>
							<p>Mot de passe</p>
							<input type="password" name="pwd" maxlength="20" placeholder="Mot de passe"><br>
							
						</div>
						<div class="subutton">
							<input type="submit" value="Valider les modifications"/>
						</div>
					</form>
				
					<div id="deletediv">
						<p> Vous pouvez aussi supprimez votre compte si vous le souhaitez </p>
						<div class="deltag">
							<a name="del">Supprimez mon compte</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<a href="#" class="go_top"></a>
	</div>
</BODY>
</HTML>
