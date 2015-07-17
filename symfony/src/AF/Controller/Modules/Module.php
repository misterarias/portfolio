<?php

namespace AF\Controller\Modules;

/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 17/07/15
 * Time: 18:04
 */
class Module {
	protected $name;
	protected $label;
	protected $path;

	// Will be shown as main title and subtitle
	protected $headerTitle;

	/**
	 * @return Module
	 */
	public function getPreviousModule() {
		return $this->previousModule;
	}

	/**
	 * @return Module
	 */
	public function getNextModule() {
		return $this->nextModule;
	}

	protected $headerSubtitle;
	/** @var Module */
	private $previousModule;
	/** @var Module */
	private $nextModule;

	public function __construct($name, $label, $path) {
		$this->name = $name;
		$this->label = $label;
		$this->path = $path;

		$this->nextModule = null;
		$this->previousModule = null;
	}

	/**
	 * @return mixed
	 */
	public function getName() {
		return $this->name;
	}

	/**
	 * @return mixed
	 */
	public function getLabel() {
		return $this->label;
	}

	/**
	 * @return mixed
	 */
	public function getPath() {
		return $this->path;
	}

	/**
	 * @param mixed $headerTitle
	 *
	 * @return Module
	 */
	public function setHeaderTitle($headerTitle) {
		$this->headerTitle = $headerTitle;

		return $this;
	}

	/**
	 * @param mixed $headerSubtitle
	 *
	 * @return Module
	 */
	public function setHeaderSubtitle($headerSubtitle) {
		$this->headerSubtitle = $headerSubtitle;

		return $this;
	}

	/**
	 * @return mixed
	 */
	public function getHeaderSubtitle() {
		return $this->headerSubtitle;
	}

	/**
	 * @return mixed
	 */
	public function getHeaderTitle() {
		return $this->headerTitle;
	}

	/**
	 * @param Module $previousModule
	 *
	 * @return Module
	 */
	public function setPreviousModule($previousModule) {
		$this->previousModule = $previousModule;

		return $this;
	}

	/**
	 * @param Module $nextModule
	 *
	 * @return Module
	 */
	public function setNextModule($nextModule) {
		$this->nextModule = $nextModule;

		return $this;
	}
}