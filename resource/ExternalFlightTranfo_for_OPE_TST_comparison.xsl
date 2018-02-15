<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" />

	<!-- copie generique des elements -->
	<xsl:template match="*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<!-- champs a filtrer -->
	<xsl:template
		match="techStamp | localID | updateTime | ENVStampsForSFPL | ADEPPoint | ADESPoint | firstSegmentPoint | lastSegmentPoint | segmentAOIExitPoint | entryAOIpoint | exitAOIpoint | trajectoryPoint | entryAOI | exitAOI | lastAPRTrackTime | TOC | TOD | theTrajectoryVersion | entryAOR | exitAOR | entryPoint | exitPoint | trajectoryPointID | compApplPoint | compTargetStart | compTargetEnd" />

	<!-- cas particulier de la trajectoire : filtrer champs id & name pour chaque 
		point -->
	<xsl:template match="trajectoryData/value/point">
		<xsl:copy>
			<xsl:for-each select="item">
				<xsl:copy>
					<xsl:copy-of select="*[not(self::id | self::name)]" />
				</xsl:copy>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
