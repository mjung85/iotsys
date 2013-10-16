<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                exclude-result-prefixes="knx"
                xmlns="http://auto.tuwien.ac.at"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:knx="http://knx.org/xml/project/11"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://auto.tuwien.ac.at schema.xsd"
                xmlns:xslFormatting="urn:xslFormatting">

  <!-- **************************************************************************** -->
  <!-- OUTPUT DEFINITION                                                            -->
  <!-- **************************************************************************** -->

  <xsl:output method="xml" indent="yes" encoding="utf-8" />

  <!-- **************************************************************************** -->
  <!-- GLOBAL PARAMETERS / VARIABLES                                                -->
  <!-- **************************************************************************** -->

  <xsl:param name="directory"/>

  <xsl:variable name="masterFile" >
    <xsl:value-of select="$directory" />/knx_master.xml
  </xsl:variable>

  <!-- **************************************************************************** -->
  <!-- NETWORK                                                                      -->
  <!-- **************************************************************************** -->

  <xsl:template match="/knx:KNX/knx:Project">
    <xsl:element name="network">

      <!-- variables -->
      <xsl:variable name="projectId" select="@Id"/>

      <xsl:variable name="standard" select='"KNX"'/>

      <xsl:variable name="projectFile">
        <xsl:value-of select="$directory"/>/<xsl:value-of select="$projectId"/>/Project.xml
      </xsl:variable>

      <xsl:variable name="projectInformation" select="document(normalize-space($projectFile))/knx:KNX/knx:Project[@Id=$projectId]/knx:ProjectInformation"/>

      <!-- attributes -->
      <xsl:copy-of select="document('')/*/@xsi:schemaLocation"/>

      <xsl:attribute name="id">
        <xsl:copy-of select="$projectId"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="$projectInformation/@Name"/>
      </xsl:attribute>

      <xsl:attribute name="standard">
        <xsl:value-of select="$standard"/>
      </xsl:attribute>

      <xsl:if test="$projectInformation/@Comment">
        <xsl:attribute name="description">
          <xsl:value-of select="$projectInformation/@Comment"/>
        </xsl:attribute>
      </xsl:if>

      <!-- elements -->
      <xsl:call-template name="entities"/>
      <xsl:call-template name="views"/>
      <xsl:call-template name="references"/>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- ENTITIES                                                                     -->
  <!-- **************************************************************************** -->

  <xsl:template name="entities">
    <xsl:element name="entities">

      <!-- elements -->
      <xsl:apply-templates select="knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line/knx:DeviceInstance"/>

    </xsl:element>
  </xsl:template>

  <xsl:template match="knx:DeviceInstance">
    <xsl:element name="entity">

      <!-- variables -->
      <xsl:variable name="deviceInstanceId">
        <xsl:value-of select="@Id"/>
      </xsl:variable>

      <xsl:variable name="productId">
        <xsl:value-of select="@ProductRefId"/>
      </xsl:variable>

      <xsl:variable name="manufacturerId">
        <xsl:value-of select="substring($productId, 1, 6)"/>
      </xsl:variable>

      <xsl:variable name="hardwareId">
        <xsl:value-of select="concat($manufacturerId, '_', substring-before(substring-after($productId, '_'),'_'))" />
      </xsl:variable>

      <xsl:variable name="hardwareFile">
        <xsl:value-of select="$directory"/>/<xsl:value-of select="$manufacturerId"/>/Hardware.xml
      </xsl:variable>

      <xsl:variable name="hardware" select="document(normalize-space($hardwareFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:Hardware/knx:Hardware[@Id=$hardwareId]/knx:Products/knx:Product[@Id=$productId]" />

      <xsl:variable name="translations" select="document(normalize-space($hardwareFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:Languages/knx:Language/knx:TranslationUnit/knx:TranslationElement[@RefId=$productId]/knx:Translation" />

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="$deviceInstanceId"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="$hardware/@Text"/>
      </xsl:attribute>

      <xsl:if test="$hardware/@VisibleDescription">
        <xsl:attribute name="description">
          <xsl:value-of select="$hardware/@VisibleDescription"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$hardware/@OrderNumber">
        <xsl:attribute name="orderNumber">
          <xsl:value-of select="$hardware/@OrderNumber"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$manufacturerId">
        <xsl:attribute name="manufacturerId">
          <xsl:value-of select="$manufacturerId"/>
        </xsl:attribute>
      </xsl:if>

      <!-- elements -->
      <xsl:call-template name="translation">
        <xsl:with-param name="translations" select="$translations"/>
        <xsl:with-param name="text" select='"name"'/>
      </xsl:call-template>

      <xsl:if test="knx:ComObjectInstanceRefs/knx:ComObjectInstanceRef/knx:Connectors">
        <xsl:element name ="datapoints">
          <xsl:apply-templates select="knx:ComObjectInstanceRefs/knx:ComObjectInstanceRef">
            <xsl:with-param name="deviceInstanceId" select="$deviceInstanceId"/>
          </xsl:apply-templates>
        </xsl:element>
      </xsl:if>


    </xsl:element>
  </xsl:template>

  <xsl:template match="knx:ComObjectInstanceRef">

    <!-- params -->
    <xsl:param name="deviceInstanceId"/>

    <!-- only create connected communication objects (knx:Connectors exists) -->
    <xsl:if test="knx:Connectors">
      <xsl:element name="datapoint">

        <!-- variables -->
        <xsl:variable name="comObjectId">
          <xsl:value-of select="@RefId" />
        </xsl:variable>

        <xsl:variable name="manufacturerId">
          <xsl:value-of select="substring($comObjectId, 1, 6)" />
        </xsl:variable>

        <xsl:variable name="applicationProgramId">
          <xsl:value-of select="concat($manufacturerId, '_', substring-before(substring-after($comObjectId, '_'),'_'))" />
        </xsl:variable>

        <xsl:variable name="comObjectFile">
          <xsl:value-of select="$directory"/>/<xsl:value-of select="$manufacturerId"/>/<xsl:value-of select="$applicationProgramId"/>.xml
        </xsl:variable>

        <xsl:variable name="comObjectRef" select="document(normalize-space($comObjectFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:ApplicationPrograms/knx:ApplicationProgram[@Id=$applicationProgramId]/knx:Static/knx:ComObjectRefs/knx:ComObjectRef[@Id=$comObjectId]" />

        <xsl:variable name="comObject" select="document(normalize-space($comObjectFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:ApplicationPrograms/knx:ApplicationProgram[@Id=$applicationProgramId]/knx:Static/knx:ComObjectTable/knx:ComObject[@Id=$comObjectRef/@RefId]" />

        <xsl:variable name="translations" select="document(normalize-space($comObjectFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:Languages/knx:Language/knx:TranslationUnit/knx:TranslationElement[@RefId=$comObjectRef/@RefId]/knx:Translation" />

        <!-- attributes -->
        <xsl:attribute name="id">
          <xsl:value-of select="concat($deviceInstanceId,'_',$comObjectId)"/>
        </xsl:attribute>

        <!-- ComObjectInstanceRef -> ComObjectRef -> ComObject -->
        <xsl:attribute name="name">
          <xsl:choose>
            <xsl:when test="@Text">
              <xsl:value-of select="@Text"/>
            </xsl:when>
            <xsl:when test="$comObjectRef/@Text">
              <xsl:value-of select="$comObjectRef/@Text"/>
            </xsl:when>
            <xsl:when test="$comObject/@Text">
              <xsl:value-of select="$comObject/@Text"/>
            </xsl:when>
          </xsl:choose>
        </xsl:attribute>

        <xsl:choose>
          <xsl:when test="@FunctionText">
            <xsl:attribute name="description">
              <xsl:value-of select="@FunctionText"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@FunctionText">
            <xsl:attribute name="description">
              <xsl:value-of select="$comObjectRef/@FunctionText"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@FunctionText">
            <xsl:attribute name="description">
              <xsl:value-of select="$comObject/@FunctionText"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:attribute name="datapointTypeIds">
          <xsl:choose>
            <xsl:when test="@DatapointType and not(@DatapointType='')">
              <xsl:value-of select="@DatapointType"/>
            </xsl:when>
            <xsl:when test="$comObjectRef/@DatapointType and not($comObjectRef/@DatapointType='')">
              <xsl:value-of select="$comObjectRef/@DatapointType"/>
            </xsl:when>
            <xsl:when test="$comObject/@DatapointType and not($comObject/@DatapointType='')">
              <xsl:value-of select="$comObject/@DatapointType"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select='"DPST-1-1"'/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>

        <xsl:attribute name="priority">
          <xsl:choose>
            <xsl:when test="@Priority">
              <xsl:value-of select="@Priority"/>
            </xsl:when>
            <xsl:when test="$comObjectRef/@Priority">
              <xsl:value-of select="$comObjectRef/@Priority"/>
            </xsl:when>
            <xsl:when test="$comObject/@Priority">
              <xsl:value-of select="$comObject/@Priority"/>
            </xsl:when>
          </xsl:choose>
        </xsl:attribute>

        <xsl:attribute name="writeFlag">
          <xsl:choose>
            <xsl:when test="@WriteFlag">
              <xsl:value-of select="@WriteFlag"/>
            </xsl:when>
            <xsl:when test="$comObjectRef/@WriteFlag">
              <xsl:value-of select="$comObjectRef/@WriteFlag"/>
            </xsl:when>
            <xsl:when test="$comObject/@WriteFlag">
              <xsl:value-of select="$comObject/@WriteFlag"/>
            </xsl:when>
          </xsl:choose>
        </xsl:attribute>

        <xsl:choose>
          <xsl:when test="@CommunicationFlag">
            <xsl:attribute name="communicationFlag">
              <xsl:value-of select="@CommunicationFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@CommunicationFlag">
            <xsl:attribute name="communicationFlag">
              <xsl:value-of select="$comObjectRef/@CommunicationFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@CommunicationFlag">
            <xsl:attribute name="communicationFlag">
              <xsl:value-of select="$comObject/@CommunicationFlag"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:choose>
          <xsl:when test="@ReadFlag">
            <xsl:attribute name="readFlag">
              <xsl:value-of select="@ReadFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@ReadFlag">
            <xsl:attribute name="readFlag">
              <xsl:value-of select="$comObjectRef/@ReadFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@ReadFlag">
            <xsl:attribute name="readFlag">
              <xsl:value-of select="$comObject/@ReadFlag"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:choose>
          <xsl:when test="@ReadOnInitFlag">
            <xsl:attribute name="readOnInitFlag">
              <xsl:value-of select="@ReadOnInitFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@ReadOnInitFlag">
            <xsl:attribute name="readOnInitFlag">
              <xsl:value-of select="$comObjectRef/@ReadOnInitFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@ReadOnInitFlag">
            <xsl:attribute name="readOnInitFlag">
              <xsl:value-of select="$comObject/@ReadOnInitFlag"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:choose>
          <xsl:when test="@TransmitFlag">
            <xsl:attribute name="transmitFlag">
              <xsl:value-of select="@TransmitFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@TransmitFlag">
            <xsl:attribute name="transmitFlag">
              <xsl:value-of select="$comObjectRef/@TransmitFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@TransmitFlag">
            <xsl:attribute name="transmitFlag">
              <xsl:value-of select="$comObject/@TransmitFlag"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <xsl:choose>
          <xsl:when test="@UpdateFlag">
            <xsl:attribute name="updateFlag">
              <xsl:value-of select="@UpdateFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObjectRef/@UpdateFlag">
            <xsl:attribute name="updateFlag">
              <xsl:value-of select="$comObjectRef/@UpdateFlag"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="$comObject/@UpdateFlag">
            <xsl:attribute name="updateFlag">
              <xsl:value-of select="$comObject/@UpdateFlag"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>

        <!-- elements -->
        <xsl:call-template name="translation">
          <xsl:with-param name="translations" select="$translations"/>
          <xsl:with-param name="text" select='"name"'/>
        </xsl:call-template>

      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- VIEWS                                                                        -->
  <!-- **************************************************************************** -->

  <xsl:template name="views">
    <xsl:element name="views">

      <!-- elements -->
      <xsl:call-template name="functional" />
      <xsl:call-template name="topology"/>
      <xsl:call-template name="building"/>
      <xsl:call-template name="domains"/>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- FUNCTIONAL VIEW                                                              -->
  <!-- **************************************************************************** -->

  <xsl:template name="functional">

    <xsl:if test="knx:Installations/knx:Installation/knx:GroupAddresses/knx:GroupRanges/knx:GroupRange">
      <xsl:element name="functional" >

        <!-- elements -->
        <xsl:for-each select="knx:Installations/knx:Installation/knx:GroupAddresses/knx:GroupRanges/knx:GroupRange">
          <xsl:call-template name="group"/>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

  </xsl:template>

  <xsl:template name="group">
    <xsl:element name="group">

      <!-- variables -->
      <xsl:variable name="groupId">
        <xsl:value-of select="@Id" />
      </xsl:variable>

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@Id"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="@Name"/>
      </xsl:attribute>

      <xsl:if test="@Description">
        <xsl:attribute name="description">
          <xsl:value-of select="@Description"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="@RangeStart">
          <xsl:attribute name="address">
            <xsl:value-of select="@RangeStart"/>
          </xsl:attribute>
        </xsl:when>
        <xsl:when test="@Address">
          <xsl:attribute name="address">
            <xsl:value-of select="@Address"/>
          </xsl:attribute>
        </xsl:when>
      </xsl:choose>

      <!-- elements -->
      <xsl:for-each select="/knx:KNX/knx:Project/knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line/knx:DeviceInstance/knx:ComObjectInstanceRefs/knx:ComObjectInstanceRef/knx:Connectors/knx:Send[@GroupAddressRefId=$groupId]">
        <xsl:call-template name="instanceGroup">
          <xsl:with-param name="connector" select='"send"'/>
        </xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="/knx:KNX/knx:Project/knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line/knx:DeviceInstance/knx:ComObjectInstanceRefs/knx:ComObjectInstanceRef/knx:Connectors/knx:Receive[@GroupAddressRefId=$groupId]">
        <xsl:call-template name="instanceGroup">
          <xsl:with-param name="connector" select='"receive"'/>
        </xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="knx:GroupRange">
        <xsl:call-template name="group"/>
      </xsl:for-each>

      <xsl:for-each select="knx:GroupAddress">
        <xsl:call-template name="group"/>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

  <xsl:template name="instanceGroup">

    <!-- parameters -->
    <xsl:param name="connector"/>

    <!-- element -->
    <xsl:element name="instance">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="concat(../../../../@Id,'_',../../@RefId)"/>
      </xsl:attribute>

      <xsl:attribute name="connector">
        <xsl:value-of select="$connector"/>
      </xsl:attribute>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- TOPOLOGY VIEW                                                                -->
  <!-- **************************************************************************** -->

  <xsl:template name="topology">

    <xsl:if test="knx:Installations/knx:Installation/knx:Topology/knx:Area">
      <xsl:element name="topology" >

        <!-- elements -->
        <xsl:for-each select="knx:Installations/knx:Installation/knx:Topology/knx:Area">
          <xsl:call-template name="area"/>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

  </xsl:template>

  <xsl:template name="area">
    <xsl:element name="area">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@Id"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="@Name"/>
      </xsl:attribute>

      <xsl:if test="@Description">
        <xsl:attribute name="description">
          <xsl:value-of select="@Description"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:attribute name="address">
        <xsl:value-of select="@Address"/>
      </xsl:attribute>

      <xsl:if test="@MediumTypeRefId">
        <xsl:attribute name="mediaTypeId">
          <xsl:value-of select="@MediumTypeRefId"/>
        </xsl:attribute>
      </xsl:if>

      <!-- elements -->
      <xsl:for-each select="knx:DeviceInstance">
        <xsl:call-template name="instanceArea"/>
      </xsl:for-each>

      <xsl:for-each select="knx:Line">
        <xsl:call-template name="area"/>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

  <xsl:template name="instanceArea">
    <xsl:element name="instance">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@Id"/>
      </xsl:attribute>

      <xsl:attribute name="address">
        <xsl:value-of select="@Address"/>
      </xsl:attribute>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- BUILDING VIEW                                                                -->
  <!-- **************************************************************************** -->

  <xsl:template name="building">

    <xsl:if test="knx:Installations/knx:Installation/knx:Buildings/knx:BuildingPart">
      <xsl:element name="building" >

        <!-- elements -->
        <xsl:for-each select="knx:Installations/knx:Installation/knx:Buildings/knx:BuildingPart">
          <xsl:call-template name="part"/>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

  </xsl:template>

  <xsl:template name="part">
    <xsl:element name="part">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@Id"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="@Name"/>
      </xsl:attribute>

      <xsl:if test="@Description">
        <xsl:attribute name="description">
          <xsl:value-of select="@Description"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:attribute name="type">
        <xsl:value-of select="@Type"/>
      </xsl:attribute>

      <!-- elements -->
      <xsl:for-each select="knx:DeviceInstanceRef">
        <xsl:call-template name="instancePart"/>
      </xsl:for-each>

      <xsl:for-each select="knx:BuildingPart">
        <xsl:call-template name="part"/>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

  <xsl:template name="instancePart">
    <xsl:element name="instance">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@RefId"/>
      </xsl:attribute>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- DOMAIN VIEW                                                                  -->
  <!-- **************************************************************************** -->

  <xsl:template name="domains">

    <xsl:if test="knx:Installations/knx:Installation/knx:Trades/knx:Trade">
      <xsl:element name="domains" >

        <!-- elements -->
        <xsl:for-each select="knx:Installations/knx:Installation/knx:Trades/knx:Trade">
          <xsl:call-template name="domain"/>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

  </xsl:template>

  <xsl:template name ="domain">
    <xsl:element name="domain">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@Id"/>
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="@Name"/>
      </xsl:attribute>

      <xsl:if test="@Description">
        <xsl:attribute name="description">
          <xsl:value-of select="@Description"/>
        </xsl:attribute>
      </xsl:if>

      <!-- elements -->
      <xsl:for-each select="knx:DeviceInstanceRef">
        <xsl:call-template name="instanceDomain"/>
      </xsl:for-each>

      <xsl:for-each select="knx:Trade">
        <xsl:call-template name="domain"/>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

  <xsl:template name="instanceDomain">
    <xsl:element name="instance">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="@RefId"/>
      </xsl:attribute>

    </xsl:element>
  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- CONFIGURATIONS                                                               -->
  <!-- **************************************************************************** -->

  <xsl:template name="references">
    <xsl:element name="references">

      <!-- elements -->
      <xsl:call-template name="datapointTypes"/>
      <xsl:call-template name="mediaTypes"/>
      <xsl:call-template name="manufacturers"/>

    </xsl:element>
  </xsl:template>

  <xsl:template name="datapointTypes">

    <!-- parameters -->
    <xsl:param name="comObjectInstanceRefs" select="knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line/knx:DeviceInstance/knx:ComObjectInstanceRefs/knx:ComObjectInstanceRef"/>
    <xsl:param name="position" select="0"/>

    <!-- elements -->
    <xsl:for-each select="document(normalize-space($masterFile))/knx:KNX/knx:MasterData/knx:DatapointTypes/knx:DatapointType">
      <xsl:call-template name="datapointType">
        <xsl:with-param name="comObjectInstanceRefs" select="$comObjectInstanceRefs"/>
        <xsl:with-param name="position" select="1" />
      </xsl:call-template>
    </xsl:for-each>

  </xsl:template>

  <xsl:template name="datapointType">

    <!-- parameters -->
    <xsl:param name="comObjectInstanceRefs"/>
    <xsl:param name="position"/>

    <!-- check, if position is less or equal the total count of communication object references -->
    <xsl:if test="$position &lt;= count($comObjectInstanceRefs)">

      <!-- load current communication object reference -->
      <xsl:variable name="comObjectInstanceRef" select="$comObjectInstanceRefs[position()=$position]"/>

      <xsl:choose>

        <!-- search for the datapoint type, if knx:Connectors exists in the current communication object reference -->
        <xsl:when test="$comObjectInstanceRef/knx:Connectors">

          <!-- variables -->
          <xsl:variable name="datapointTypeId">
            <xsl:value-of select="@Id" />
          </xsl:variable>

          <xsl:variable name="comObjectId">
            <xsl:value-of select="$comObjectInstanceRef/@RefId" />
          </xsl:variable>

          <xsl:variable name="manufacturerId">
            <xsl:value-of select="substring($comObjectId, 1, 6)" />
          </xsl:variable>

          <xsl:variable name="applicationProgramId">
            <xsl:value-of select="concat($manufacturerId, '_', substring-before(substring-after($comObjectId, '_'),'_'))" />
          </xsl:variable>

          <xsl:variable name="comObjectFile">
            <xsl:value-of select="$directory"/>/<xsl:value-of select="$manufacturerId"/>/<xsl:value-of select="$applicationProgramId"/>.xml
          </xsl:variable>

          <xsl:variable name="comObjectRef" select="document(normalize-space($comObjectFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:ApplicationPrograms/knx:ApplicationProgram[@Id=$applicationProgramId]/knx:Static/knx:ComObjectRefs/knx:ComObjectRef[@Id=$comObjectId]" />

          <xsl:variable name="comObject" select="document(normalize-space($comObjectFile))/knx:KNX/knx:ManufacturerData/knx:Manufacturer[@RefId=$manufacturerId]/knx:ApplicationPrograms/knx:ApplicationProgram[@Id=$applicationProgramId]/knx:Static/knx:ComObjectTable/knx:ComObject[@Id=$comObjectRef/@RefId]" />

          <xsl:variable name="datapointTypeIds">
            <xsl:choose>
              <xsl:when test="$comObjectInstanceRef/@DatapointType and not($comObjectInstanceRef/@DatapointType='')">
                <xsl:value-of select="$comObjectInstanceRef/@DatapointType"/>
              </xsl:when>
              <xsl:when test="$comObjectRef/@DatapointType and not($comObjectRef/@DatapointType='')">
                <xsl:value-of select="$comObjectRef/@DatapointType"/>
              </xsl:when>
              <xsl:when test="$comObject/@DatapointType and not($comObject/@DatapointType='')">
                <xsl:value-of select="$comObject/@DatapointType"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select='"DPST-1-1"'/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <!-- elements and attributes -->
          <xsl:choose>

            <!-- create reference, if datapoint subtype is used by the current communication object reference -->
            <xsl:when test="contains(concat($datapointTypeIds,' '),concat(@Id,' '))">
              <xsl:call-template name="reference">
                <xsl:with-param name="id" select="@Id"/>
                <xsl:with-param name="name" select="@Name"/>
                <xsl:with-param name="description" select="@Text"/>
              </xsl:call-template>
            </xsl:when>

            <!-- jump to datapoint subtypes, if the datapoint is used by the current communication object reference -->
            <xsl:when test="contains(translate(concat($datapointTypeIds,' '),'DPST','DPT'), concat(@Id,'-')) or contains(concat($datapointTypeIds,' '), concat(@Id,' '))">

              <xsl:if test="contains(concat($datapointTypeIds,' '), concat(@Id,' '))">
                <xsl:call-template name="reference">
                  <xsl:with-param name="id" select="@Id"/>
                  <xsl:with-param name="name" select="@Name"/>
                  <xsl:with-param name="description" select="@Text"/>
                </xsl:call-template>
              </xsl:if>

              <xsl:if test="knx:DatapointSubtypes/knx:DatapointSubtype">
                <xsl:for-each select="knx:DatapointSubtypes/knx:DatapointSubtype">
                  <xsl:call-template name="datapointType">
                    <xsl:with-param name="comObjectInstanceRefs" select="$comObjectInstanceRefs"/>
                    <xsl:with-param name="position" select="1"/>
                  </xsl:call-template>
                </xsl:for-each>
              </xsl:if>
            </xsl:when>

            <!-- otherwise, compare the datapoint type with the next communication object reference -->
            <xsl:otherwise>
              <xsl:call-template name="datapointType">
                <xsl:with-param name="comObjectInstanceRefs" select="$comObjectInstanceRefs"/>
                <xsl:with-param name="position" select="$position + 1"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:when>

        <!-- otherwise, compare the datapoint type with the next communication object reference -->
        <xsl:otherwise>

          <xsl:call-template name="datapointType">
            <xsl:with-param name="comObjectInstanceRefs" select="$comObjectInstanceRefs"/>
            <xsl:with-param name="position" select="$position + 1"/>
          </xsl:call-template>

        </xsl:otherwise>

      </xsl:choose>
    </xsl:if>

  </xsl:template>

  <xsl:template name="mediaTypes">

    <!-- parameters -->
    <xsl:param name="lines" select="knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line"/>

    <!-- elements -->
    <xsl:apply-templates select="document(normalize-space($masterFile))/knx:KNX/knx:MasterData/knx:MediumTypes/knx:MediumType">
      <xsl:with-param name="lines" select="$lines"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="knx:MediumType">

    <!-- parameters -->
    <xsl:param name="lines"/>

    <!-- variables -->
    <xsl:variable name="mediumTypeId">
      <xsl:value-of select="@Id"/>
    </xsl:variable>

    <!-- create element, if medium type is used in at least one topology line -->
    <xsl:if test="$lines[@MediumTypeRefId=$mediumTypeId]">
      <xsl:call-template name="reference">
        <xsl:with-param name="id" select="@Id"/>
        <xsl:with-param name="name" select="@Name"/>
        <xsl:with-param name="description" select="@Text"/>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <xsl:template name="manufacturers">

    <!-- parameters -->
    <xsl:param name="deviceInstances" select="knx:Installations/knx:Installation/knx:Topology/knx:Area/knx:Line/knx:DeviceInstance"/>

    <!-- elements -->
    <xsl:apply-templates select="document(normalize-space($masterFile))/knx:KNX/knx:MasterData/knx:Manufacturers/knx:Manufacturer">
      <xsl:with-param name="deviceInstances" select="$deviceInstances"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="knx:Manufacturer">

    <!-- parameters -->
    <xsl:param name="deviceInstances"/>

    <!-- variables -->
    <xsl:variable name="manufacturerId">
      <xsl:value-of select="@Id"/>
    </xsl:variable>

    <!-- create reference, if manufacturer is used in at least one device -->
    <xsl:if test="$deviceInstances[substring(@ProductRefId, 1, 6)=$manufacturerId]">
      <xsl:call-template name="reference">
        <xsl:with-param name="id" select="@Id"/>
        <xsl:with-param name="name" select="@Name"/>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <xsl:template name="reference">

    <!-- parameters -->
    <xsl:param name="name"/>
    <xsl:param name="description"/>
    <xsl:param name="id"/>

    <xsl:element name="reference">

      <!-- attributes -->
      <xsl:attribute name="id">
        <xsl:value-of select="$id"/>
      </xsl:attribute>

      <xsl:if test="$name">
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$description">
        <xsl:attribute name="description">
          <xsl:value-of select="$description"/>
        </xsl:attribute>
      </xsl:if>

    </xsl:element>

  </xsl:template>

  <!-- **************************************************************************** -->
  <!-- LANGUAGES                                                                    -->
  <!-- **************************************************************************** -->

  <xsl:template name="translation">

    <!-- parameters -->
    <xsl:param name="translations"/>
    <xsl:param name="text"/>

    <!-- create if not empty -->
    <xsl:if test="$translations">
      <xsl:element name="translations">

        <!-- elements -->
        <xsl:for-each select="$translations">
          <xsl:element name="translation">

            <!-- attributes -->
            <xsl:attribute name="language">
              <xsl:value-of select="../../../@Identifier"/>
            </xsl:attribute>

            <xsl:attribute name="attribute">
              <xsl:choose>
                <xsl:when test="@AttributeName='Text'">
                  <xsl:value-of select="$text"/>
                </xsl:when>
                <xsl:when test="@AttributeName='VisibleDescription'">
                  <xsl:value-of select='"description"'/>
                </xsl:when>
                <xsl:when test="@AttributeName='Name'">
                  <xsl:value-of select='"name"'/>
                </xsl:when>
                <xsl:when test="@AttributeName='FunctionText'">
                  <xsl:value-of select='"description"'/>
                </xsl:when>
              </xsl:choose>
            </xsl:attribute>

            <xsl:attribute name="value">
              <xsl:value-of select="@Text"/>
            </xsl:attribute>

          </xsl:element>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>

