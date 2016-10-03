<!DOCTYPE html >
<HTML>
<title>Accueil</title>
<HEAD>
<meta charset="utf-8" />
<!--Lien vers la mise en page CSS-->
<link href="css/accueil.css" rel="stylesheet" TYPE="text/css">
<link href="css/Comments.css" rel="stylesheet" TYPE="text/css">
<link href="css/Overlay.css" rel="stylesheet" TYPE="text/css">

<meta name="viewport" content="width=device-width, initial-scale=1">
<!--Lien vers JQuerry-->
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js"></script>
<!--Lien vers le javaScript-->
<script type="text/javascript" src="js/accueil.js"></script>
<script type="text/javascript" src="js/Util.js"></script>
<script type="text/javascript" src="js/Overlay.js"></script>

</HEAD>
<BODY>
	<script type="text/javascript">
		function go() {
			<%String id = request.getParameter("id");
			String login = request.getParameter("login");
			String key = request.getParameter("key");
			if (id != null) {
				out.println("main(" + id + ",'" + login + "','" + key + "')");
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
					<a class="tohighlight" href="javascript:window.location.reload()"
						name='accueil'>Accueil</a> <a class="nothightlight"
						name='settings'>Param&egravetres</a>
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
				<form id="formulaire" name="postcommentaire" action="">
					<div class="newcomm">
						<textarea name="writecomment"
							placeholder="Entrez votre commentaire ici"></textarea>
						<div class="postbutton">
							<input type="submit" name="postbutton" value="Poster"> <Label
								for='numchar'> 0/140 Caract&egraveres</Label>
						</div>
					</div>
				</form>
				<div id="commcontainer">
				</div>
			</div>
		</div>
		<a href="#" class="go_top"></a>
	</div>
</BODY>

</HTML>

