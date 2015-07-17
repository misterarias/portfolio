<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 12:43
 */

namespace AF\ProjectBundle\Actors\Social;


class Social {
	private $url;
	private $icon;

	/**
	 * @return mixed
	 */
	public function getIcon() {
		return $this->icon;
	}

	public function __construct($icon, $url) {

		$this->url = $url;
		$this->icon = $icon;
	}

	/**
	 * @return mixed
	 */
	public function getUrl() {
		return $this->url;
	}
}