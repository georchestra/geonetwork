Eléments modifiés ou corrigés dans la version 2 du profil français:

- la partie concernant les feature catalogues a été modifiée selon l'amendement de ISO19110. Désormais, le profil français utilise directement 
le concept de feature catalogue décrit dans l'amendement et non plus une extension du concept abstrait de catalogue (CT_Catalogue). 

- l'attribut isoType a été rajouté sur tous les éléments du profil français étendant la norme ISO19115 (l'encodage XML de ces extensions est décrit 
dans le fichier fra.xsd). Dans une instantiation XML du profil français, tout élément correspondant à une extension de ISO19115 doit avoir un 
attribut isoType renseignant le type ISO19115 dont il dérive.

Par exemple, un fichier XML conforme au profil français pourrait contenir l'élément FRA_LegalConstraints ayant les propriétés suivantes (les valeurs 
indiquées ont valeur d'exemple uniquement) :

<resourceConstraints>
	<!-- Portions de métadonnées non montrées -->
	<FRA_LegalConstraints isoType="MD_LegalConstraints">
		<useLimitation>
			<gco:CharacterString>Utilisation non commerciale uniquement</CharacterString>
		</useLimitation>
		<accessConstraints>
			<MD_RestrictionCode codeList="./CodeLists.xml#codelist12" codeListValue="copyright"/>
		</accessConstraints>
		<useConstraints>
			<MD_RestrictionCode codeList="./CodeLists.xml#codelist12" codeListValue="license"/>
		</useConstraints>
		<otherConstraints/>
		<citation>
			<title>
				<gco:CharacterString>Document de référence relatif aux contraintes s'appliquant aux données</gco:CharacterString>
			</title>
			<date>
				<CI_Date>
					<date><gco:Date>1992-01-01</gco:Date></date>
					<dateType> <CI_DateTypeCode codeList="./CodeLists.xml#codelist2" codeListValue="publication"/></dateType>
				</CI_Date> 
			</date>
		</citation>
	</FRA_LegalConstraints>
	<!-- Portions de métadonnées non montrées -->
</resourceConstraints>


- l'attribut fraType a été défini dans le fichier fra.xsd. Lors du développement de profils du profil français, cet attribut doit être utilisé pour indiquer 
les extensions au profil français. 


Prenons l'exemple d'une extension de FRA_LegalConstraints dénommée BLA_LegalConstraints qui serait décrite dans le fichier bla.xsd :

-- l'espace de nommage associé serait par exemple bla:http://mysite.com/bla.

-- bla.xsd devrait importer fra.xsd.

-- bla.xsd devrait indiquer sur toutes les extensions au profil français la nécessité d'utiliser l'attribut fraType, de même que fra.xsd indique 
sur toutes les extensions à ISO19115 la nécessité d'utiliser l'attribut isoType.

-- Par dérivation, toute classe étendue d'une classe du profil français étendant ISO19115 devrait également posséder un attribut isoType.


-- une instantiation XML de BLA_LegalConstraints pourrait alors ressembler à :

<resourceConstraints>
	<!-- Portions de métadonnées non montrées -->
	<BLA_LegalConstraints isoType="MD_LegalConstraints" fraType="FRA_LegalConstraints">
	<!-- Portions de métadonnées non montrées -->
	</BLA_LegalConstraints>
	<!-- Portions de métadonnées non montrées -->
</resourceConstraints>