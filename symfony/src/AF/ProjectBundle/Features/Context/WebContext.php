<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 4/05/15
 * Time: 18:51
 */

namespace AF\ProjectBundle\Features\Context;

class WebContext extends BaseWebContext {

	/**
	 * @Then /^I should find a navigation bar with (\d+) items$/
	 * @param $nItems
	 */
	public function iShouldFindANavigationBarWithItems($nItems) {
		$this->assertSession()->elementExists("css", "#navbar");
		$this->assertSession()->elementsCount("css", "#navbar li", $nItems);
	}

	/**
	 * @Then /^I should find a navigation bar entry named \'([^\']*)\'$/
	 * @param $arg1 Name of element
	 * @throws \Behat\Mink\Exception\ElementNotFoundException
	 */
	public function iShouldFindANavigationBarEntryNamed($arg1) {
		$this->assertSession()->elementExists("xpath", "//div['@id=navbar']/ul/li/a[contains(text(),'$arg1')]");
	}

	/**
	 * @Then /^I should find a navigation bar entry named \'([^\']*)\' with link \'([^\']*)\'$/
	 */
	public function iShouldFindANavigationBarEntryNamedWithLink($arg1, $arg2) {
		$this->assertSession()->elementExists("xpath", "//div['@id=navbar']/ul/li/a[contains(text(),'$arg1')]");
		$href = $this->getPage()->find("xpath", "//div['@id=navbar']/ul/li/a[contains(text(),'$arg1')]/@href");
		var_dump($href);
	}

	/**
	 * @Then /^I should find a graph$/
	 */
	public function iShouldFindAGraph() {
		usleep(1000);
		$this->assertSession()->elementExists("css", "div#main_graph svg.canvas");
	}
}