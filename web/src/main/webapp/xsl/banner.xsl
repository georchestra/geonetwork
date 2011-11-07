<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!--
	  main html banner
  -->
  <xsl:template name="banner">

    <!-- integrating geOrchestra header -->
    <xsl:if test="string(/root/request/noheader/text())!='true'">
    <link href='/static/css/header.css' rel='stylesheet' type='text/css' />
    <div id="go_head">
        <a href="/" id="go_home" title="retourner à l’accueil">
            <img src="/static/img/logo.png" alt="geOrchestra" height="50"/>
        </a>
        <ul>
            <li class="active"><a href="/geonetwork/srv/fr/main.home">catalogue</a></li>
            <li><a href="/mapfishapp/">visualiseur</a></li>
            <!-- TODO: check user groups to show/hide editor and phpldapadmin items in this list -->
            <li><a href="/mapfishapp/edit">éditeur</a></li>
            <li><a href="/extractorapp/">extracteur</a></li>
            <li><a href="/geoserver/web/">services</a></li>
            <li><a href="/phpldapadmin">utilisateurs</a></li>
          </ul>
          <xsl:choose>
          <xsl:when test="string(/root/gui/login/userId)=''">
            <p class="logged">
                <a href="?login">connexion</a>
            </p>
          </xsl:when>
          <xsl:otherwise>
            <p class="logged">
                <xsl:value-of select="/root/gui/login/username"/><span class="light"> | </span><a href="/j_spring_security_logout">déconnexion</a>
            </p>
          </xsl:otherwise>
        </xsl:choose>
    </div>
	</xsl:if>
	    

	<table width="100%">
	  <!-- buttons -->
	  <tr class="banner">
		<td class="banner-menu" width="380px">
		  <a class="banner" href="{/root/gui/locService}/main.home"><xsl:value-of select="/root/gui/strings/home"/></a>
		  |
		  <!--		//FIXME			<xsl:if test="string(/root/gui/results)!=''">
			  <xsl:choose>
			  <xsl:when test="/root/gui/reqService='main.present'">
			  <font class="banner-active"><xsl:value-of select="/root/gui/strings/result"/></font>
			  </xsl:when>
			  <xsl:otherwise>
			  <a class="banner" href="{/root/gui/locService}/main.present"><xsl:value-of select="/root/gui/strings/result"/></a>
			  </xsl:otherwise>
			  </xsl:choose>
			  |
			  </xsl:if> -->
		  <xsl:if test="string(/root/gui/login/userId)!=''">
			<xsl:choose>
			  <xsl:when test="/root/gui/reqService='admin'">
				<font class="banner-active"><xsl:value-of select="/root/gui/strings/admin"/></font>
			  </xsl:when>
			  <xsl:otherwise>
				<a class="banner" href="{/root/gui/locService}/admin"><xsl:value-of select="/root/gui/strings/admin"/></a>
			  </xsl:otherwise>
			</xsl:choose>
			|
		  </xsl:if>
		  <xsl:choose>
			<xsl:when test="/root/gui/reqService='feedback'">
			  <font class="banner-active"><xsl:value-of select="/root/gui/strings/contactUs"/></font>
			</xsl:when>
			<xsl:otherwise>
			  <a class="banner" href="{/root/gui/locService}/feedback"><xsl:value-of select="/root/gui/strings/contactUs"/></a>
			</xsl:otherwise>
		  </xsl:choose>
		  |
		  <xsl:choose>
			<xsl:when test="/root/gui/reqService='links'">
			  <font class="banner-active"><xsl:value-of select="/root/gui/strings/links"/></font>
			</xsl:when>
			<xsl:otherwise>
			  <a class="banner" href="{/root/gui/locService}/links"><xsl:value-of select="/root/gui/strings/links"/></a>
			</xsl:otherwise>
		  </xsl:choose>
		  <xsl:if test="string(/root/gui/login/userId)='' and
						string(/root/gui/env/userSelfRegistration/enable)='true'">
			|
			<xsl:choose>
			  <xsl:when test="/root/gui/reqService='user.register.get'">
				<font class="banner-active"><xsl:value-of select="/root/gui/strings/register"/></font>
			  </xsl:when>
			  <xsl:otherwise>
				<a class="banner" href="{/root/gui/locService}/user.register.get"><xsl:value-of select="/root/gui/strings/register"/></a>
			  </xsl:otherwise>
			</xsl:choose>
		  </xsl:if>
		  |
		  <xsl:choose>
			<xsl:when test="/root/gui/reqService='about'">
			  <font class="banner-active"><xsl:value-of select="/root/gui/strings/about"/></font>
			</xsl:when>
			<xsl:otherwise>
			  <a class="banner" href="{/root/gui/locService}/about"><xsl:value-of select="/root/gui/strings/about"/></a>
			</xsl:otherwise>
		  </xsl:choose>
		  |
		  <!--					<xsl:choose>
			  <xsl:when test="/root/gui/reqService='help'">
			  <font class="banner-active"><xsl:value-of select="/root/gui/strings/help"/></font>
			  </xsl:when>
			  <xsl:otherwise>
			  <a class="banner" href="{/root/gui/locService}/help"><xsl:value-of select="/root/gui/strings/help"/></a>
			  </xsl:otherwise>
			  </xsl:choose> -->

		  <a class="banner" href="/doc/html/documentation.html#catalogue" target="_blank"><xsl:value-of select="/root/gui/strings/help"/></a>
		  |
		</td>
		<td align="right" class="banner-menu" width="610px">
		  <xsl:if test="count(/root/gui/config/languages/*) &gt; 1">
			<!-- Redirect to current page when no error could happen
				 (ie. when having no parameters in GET), if not redirect to the home page. -->
			<xsl:variable name="redirectTo">
			  <xsl:choose>
				<xsl:when test="/root/gui/reqService='metadata.show'">main.home</xsl:when>
				<!-- TODO : Add other exception ? -->
				<xsl:otherwise><xsl:value-of select="/root/gui/reqService"/></xsl:otherwise>
			  </xsl:choose>
			</xsl:variable>

			<select class="banner-content content">
			  <xsl:attribute name="onchange">location.replace('../' + this.options[this.selectedIndex].value + '/<xsl:value-of select="$redirectTo"/>');</xsl:attribute>
			  <xsl:for-each select="/root/gui/config/languages/*">
				<xsl:variable name="lang" select="name(.)"/>
				<option value="{$lang}">
				  <xsl:if test="/root/gui/language=$lang">
					<xsl:attribute name="selected">selected</xsl:attribute>
				  </xsl:if>
				  <xsl:value-of select="/root/gui/strings/*[name(.)=$lang]"/>
				</option>
			  </xsl:for-each>
			</select>
		  </xsl:if>
		</td>
			</tr>

			<!-- FIXME: should also contain links to last results and metadata -->

			<!-- login -->
            
			<xsl:if test="string(/root/request/noheader/text())='true'">
			<tr class="banner">
				<td class="banner-login" align="right" width="380px">
				</td>
				<xsl:choose>
					<xsl:when test="string(/root/gui/login/userId)!=''">
						<td align="right" class="banner-login">

						<div id="jpegPhoto-tip">
						<script language="JavaScript" type="text/javascript">
						Ext.onReady(function(){
								new Ext.ToolTip({
									target: 'jpegPhoto-tip',
									autoLoad: {
										url: '/geonetwork/srv/fr/jpegphoto.get',
										method: 'GET',
										params : { uid : '<xsl:value-of select="/root/gui/login/name"/>' }
									},
									dismissDelay: 15000
								});
								Ext.QuickTips.init();
						});
						</script>
						<xsl:value-of select="/root/gui/strings/user"/>
						<xsl:text>: </xsl:text>
						<xsl:value-of select="/root/gui/login/name"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="/root/gui/login/surname"/>
						<xsl:text> </xsl:text>
						</div>
						<button class="banner" onclick="document.location='/j_spring_security_logout';"><xsl:value-of select="/root/gui/strings/logout"/></button>
						</td>

					</xsl:when>
					<xsl:otherwise>
						<td align="right" class="banner-login">
						<a class="button" href="/geonetwork/?login">login</a>
						</td>
					</xsl:otherwise>
				</xsl:choose>
			</tr>
		</xsl:if>
		</table>
	</xsl:template>

	<!--
	main html banner in a popup window
	-->
	<xsl:template name="bannerPopup">

		<table width="100%">

			<!-- title -->
			<!-- TODO : Mutualize with main banner template -->
			<tr class="banner">
				<td class="banner">
					<img src="{/root/gui/url}/images/header-left.jpg" alt="GeoNetwork opensource" align="top" />
				</td>
				<td align="right" class="banner">
					<img src="{/root/gui/url}/images/header-right.gif" alt="World picture" align="top" />
				</td>
			</tr>

			<!-- buttons -->
			<tr class="banner">
				<td class="banner-menu" colspan="2">
				</td>
			</tr>

			<tr class="banner">
				<td class="banner-login" colspan="2">
				</td>
			</tr>
		</table>
	</xsl:template>


</xsl:stylesheet>

