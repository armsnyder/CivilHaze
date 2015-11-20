'use strict';

describe("Come Again", function() {
    var $httpBackend,
        $scope;

    beforeEach(module('comeAgain'));

    beforeEach(inject(function($rootScope) {
        $scope = $rootScope;
    }));

    beforeEach(inject(function(_$httpBackend_) {
        $httpBackend = _$httpBackend_;
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it("should route calls to the correct server", function() {

    })
});