(function() {
	var app = angular.module("documentationApp", [], function($locationProvider) {
        $locationProvider.html5Mode(true);
  	});

	app.controller("DocumentationController", function($scope, $location) {
		this.pages = pages;

		$scope.getPageName = function() {
			if (pages[$location.search().page] === undefined) {
				return "Documentation";
			} else {
				return "Documentation: " + pages[$location.search().page].name;
			}
		};

		$scope.getPageLink = function() {
			return "/SMBLevelWorkshop/documentation/" + $location.search().page + ".html";
		};

		$scope.isPageIDSelected = function(id) {
			return id === $location.search().page;
		};
	});

	var pages = {
		"gettingStartedWithModding": {
			"name": "Getting Started with Monkey Ball modding"
		},
		"gettingStarted": {
			"name": "Getting Started with SMB Level Workshop"
		},
		"stagedefFormat1": {
			"name": "SMB 1 StageDef File Format"
		},
		"stagedefFormat2": {
			"name": "SMB 2 StageDef File Format"
		},
		"gmaFormat": {
			"name": "GMA File Format"
		},
		"tplFormat12": {
			"name": "SMB 1 / 2 TPL File Format"
		},
		"tplFormatDx": {
			"name": "SMB Deluxe TPL File Format"
		},
		"sfxFormat": {
			"name": "Sound Effects File Formats"
		},
		"replayFormat1": {
			"name": "SMB 1 Replay File Format"
		},
		"replayFormat2": {
			"name": "SMB 2 Replay File Format"
		},
		"debugShortcuts": {
			"name": "Debug Shortcuts"
		},
		"rmacGlitch": {
			"name": "The Rmac Glitch (Instant Goal)"
		},
		"removingWorldEffects": {
			"name": "Removing World Effects"
		},
		"modifyingStageOrder": {
			"name": "Modifying Stage Order"
		}
	};
})();
