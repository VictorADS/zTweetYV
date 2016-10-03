<!DOCTYPE html >
<HTML>
<title>Profil</title>
<HEAD>
<meta charset="utf-8" />
<!--Lien vers la mise en page CSS-->
<link href="css/profile.css" rel="stylesheet" TYPE="text/css">
<link href="css/Comments.css" rel="stylesheet" TYPE="text/css">
<link href="css/Overlay.css" rel="stylesheet" TYPE="text/css">

<meta name="viewport" content="width=device-width, initial-scale=1">
<!--Lien vers JQuerry-->
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js"></script>
<!--Lien vers le javaScript-->
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/Util.js"></script>
<script type="text/javascript" src="js/Overlay.js"></script>

</HEAD>
<BODY>
	<script type="text/javascript">
		function go(){
			<%
			String key = request.getParameter("keyUserSession");
			String loginProfile= request.getParameter("loginAuteurCommentaire");
			if (key != null) {
				out.println("main('"+loginProfile+"','"+key+"')");
			}else{
				out.println("main('"+loginProfile+"')");
			}
			%>
		}
		$(go);
	</script>



	<div id="wrapper">
		<div id="fixe">

			<div class="navbar">
				<div id="logo">
					<a class="nothightlight" name='accueil'>Accueil</a> <a
						class="nothightlight" name='settings'>Param&egravetres</a>
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

			<div id="Statistiques" class="left"></div>

			<div class="middle">

				<div id="commcontainer"></div>

			</div>

		</div>
		<a href="#" class="go_top"></a>
	</div>
</BODY>

</HTML>

