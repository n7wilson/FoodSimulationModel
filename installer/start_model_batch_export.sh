#!/bin/bash

# The version of Repast Simphony being used.
export VERSION=2.0.1

# The installed path of Repast. Quotes may be necessary if there is a space character in the path.
export REPAST="/Applications/Repast-Simphony-2.0"

# The installed path of Eclipse. Quotes may be necessary if there is a space character in the path.
export ECLIPSE=$REPAST/eclipse

# The plugins path of Eclipse.
export PLUGINS=$ECLIPSE/plugins
export REPAST_SIMPHONY_LIB=$PLUGINS/repast.simphony.runtime_$VERSION/lib

# The Eclipse workspace containing the Repast model.
export WORKSPACE=~/Documents/workspace

# The name of the model. This might be case-sensitive. This is the name of your package. It should be the package at the top of all your .java files and match the "package" listed in your model.score file (when viewed as a text file).
export MODELNAME=FoodSimulationModel

# The folder of the model. This might be case-sensitive. This is the base folder of your project in the file system.
export MODELFOLDER=$WORKSPACE/$MODELNAME

# The file containing the batch parameters. For some additional information, see Repast documentation of batch parameters at http://repast.sourceforge.net/docs/reference/SIM/Batch%20Parameters.html and/or an example batch_params.xml file at http://www.pamelatoman.net/blog/2010/08/sample-batchparamsxml/.
# The following is the usual location within your Repast workspace - change according to your location / system
export BATCHPARAMS=$MODELFOLDER/batch/test_batch_params.xml

# Change to the root folder of your project so that the relative paths are handled correctly
cd $MODELFOLDER

# Execute in batch mode.
java -classpath $MODELFOLDER/lib/*:$PLUGINS/repast.simphony.gui_$VERSION/bin:$PLUGINS/saf.core.ui_$VERSION/*:$PLUGINS/saf.core.ui_$VERSION/lib/*:$PLUGINS/repast.simphony.relogo.runtime_$VERSION/lib/*:$PLUGINS/repast.simphony.relogo.ide_$VERSION/libs/*:$MODELFOLDER/bin:$PLUGINS/repast.simphony.groovy_$VERSION/bin:$PLUGINS/repast.simphony.relogo.runtime_$VERSION/bin:$PLUGINS/repast.simphony.relogo.ide_$VERSION/bin:$PLUGINS/repast.simphony.scenario_$VERSION/bin:$PLUGINS/repast.simphony.batch_$VERSION/bin:$PLUGINS/repast.simphony.runtime_$VERSION/lib/*:$PLUGINS/repast.simphony.runtime_$VERSION/bin:$PLUGINS/repast.simphony.core_$VERSION/lib/*:$PLUGINS/repast.simphony.core_$VERSION/bin:$PLUGINS/repast.simphony.data_$VERSION/lib/*:$PLUGINS/repast.simphony.score.runtime_$VERSION/lib/*:$PLUGINS/repast.simphony.score.runtime_$VERSION/bin:$PLUGINS/repast.simphony.dataLoader_$VERSION/bin:$PLUGINS/repast.simphony.data_$VERSION/bin:$PLUGINS/repast.simphony.score_$VERSION/bin:$REPAST_SIMPHONY_LIB/saf.core.runtime.jar:$REPAST_SIMPHONY_LIB/commons-logging-1.0.4.jar:$REPAST_SIMPHONY_LIB/groovy-all-1.5.7.jar:$REPAST_SIMPHONY_LIB/javassist-3.7.0.GA.jar:$REPAST_SIMPHONY_LIB/jpf.jar:$REPAST_SIMPHONY_LIB/jpf-boot.jar:$REPAST_SIMPHONY_LIB/log4j-1.2.13.jar:$REPAST_SIMPHONY_LIB/xpp3_min-1.1.4c.jar:$REPAST_SIMPHONY_LIB/xstream-1.3.jar:$PLUGINS/repast.simphony.userpanel.ui_2.0.1.jar repast.simphony.batch.BatchMain -params $BATCHPARAMS $MODELFOLDER/$MODELNAME.rs