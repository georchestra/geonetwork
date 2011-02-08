El�ments modifi�s ou corrig�s dans la version 2 du profil fran�ais:

- la partie concernant les feature catalogues a �t� modifi�e selon l'amendement de ISO19110. D�sormais, le profil fran�ais utilise directement 
le concept de feature catalogue d�crit dans l'amendement et non plus une extension du concept abstrait de catalogue (CT_Catalogue). 

- l'attribut isoType a �t� rajout� sur tous les �l�ments du profil fran�ais �tendant la norme ISO19115 (l'encodage XML de ces extensions est d�crit 
dans le fichier fra.xsd). Dans une instantiation XML du profil fran�ais, tout �l�ment correspondant � une extension de ISO19115 doit avoir un 
attribut isoType renseignant le type ISO19115 dont il d�rive.

Par exemple, un fichier XML conforme au profil fran�ais pourrait contenir l'�l�ment FRA_LegalConstraints ayant les propri�t�s suivantes (les valeurs 
indiqu�es ont valeur d'exemple uniquement) :

<resourceConstraints>
	<!-- Portions de m�tadonn�es non montr�es -->
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
				<gco:CharacterString>Document de r�f�rence relatif aux contraintes s'appliquant aux donn�es</gco:CharacterString>
			</title>
			<date>
				<CI_Date>
					<date><gco:Date>1992-01-01</gco:Date></date>
					<dateType> <CI_DateTypeCode codeList="./CodeLists.xml#codelist2" codeListValue="publication"/></dateType>
				</CI_Date> 
			</date>
		</citation>
	</FRA_LegalConstraints>
	<!-- Portions de m�tadonn�es non montr�es -->
</resourceConstraints>


- l'attribut fraType a �t� d�fini dans le fichier fra.xsd. Lors du d�veloppement de profils du profil fran�ais, cet attribut doit �tre utilis� pour indiquer 
les extensions au profil fran�ais. 


Prenons l'exemple d'une extension de FRA_LegalConstraints d�nomm�e BLA_LegalConstraints qui serait d�crite dans le fichier bla.xsd :

-- l'espace de nommage associ� serait par exemple bla:http://mysite.com/bla.

-- bla.xsd devrait importer fra.xsd.

-- bla.xsd devrait indiquer sur toutes les extensions au profil fran�ais la n�cessit� d'utiliser l'attribut fraType, de m�me que fra.xsd indique 
sur toutes les extensions � ISO19115 la n�cessit� d'utiliser l'attribut isoType.

-- Par d�rivation, toute classe �tendue d'une classe du profil fran�ais �tendant ISO19115 devrait �galement poss�der un attribut isoType.


-- une instantiation XML de BLA_LegalConstraints pourrait alors ressembler � :

<resourceConstraints>
	<!-- Portions de m�tadonn�es non montr�es -->
	<BLA_LegalConstraints isoType="MD_LegalConstraints" fraType="FRA_LegalConstraints">
	<!-- Portions de m�tadonn�es non montr�es -->
	</BLA_LegalConstraints>
	<!-- Portions de m�tadonn�es non montr�es -->
</resourceConstraints>