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
	 * TODO USe this function as a fallback case
	 */
	public function saveCurrentPage() {
		$pageHtml = $this->getSession()->getPage()->getHtml();
		$pagePng = $this->getSession()->getScreenshot();
		$randSeed = rand(1000000, 20000000);
		file_put_contents("screenie$randSeed.png", $pagePng);
		file_put_contents("screenie$randSeed.html", $pageHtml);

	}

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
//		$href = $this->getPage()->find("xpath", "//div['@id=navbar']/ul/li/a[contains(text(),'$arg1')]/@href");
		//	\PHPUnit_Framework_Assert::assertEquals($href, $arg2);
	}

	/**
	 * @Then /^I should find a graph$/
	 */
	public function iShouldFindAGraph() {
		$this->assertSession()->elementExists("xpath", "//div['@id=main_graph']/*[name()='svg']/*[name()='rect']");
	}
}