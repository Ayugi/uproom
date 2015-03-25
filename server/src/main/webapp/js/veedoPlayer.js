window.VeedoPlayer = {
	_api_domain : 'http://account.veedo.ru',
	_options : {},
	_player : false,
	
	init : function(selector, camera_stream, options){
		this._options.width = (options && options.width) ? options.width : 600;
		this._options.height = (options && options.height) ? options.height : 480;
		this._options.controls = (options && options.controls) ? true : false;
		this._options.autoPlay = (options && options.autoPlay) ? true : false;

		this._options.selector = selector;
		this._options.camera_stream = camera_stream;
		this._options.server = false;

		// isset jquery
		if(window.$ !== undefined){
			this._init()
		}else{
			VeedoPlayer.include(this._api_domain + '/template/account/js/jquery-2.0.3.min.js', this._init);
		}
	},

	_init : function(){
		// include js and css
		$('head').append(
			$('<link>').attr({'type' : 'text/css', 'rel' : 'stylesheet', 'href' : VeedoPlayer._api_domain + '/template/account/css/flowplayer.css'})
		)
		VeedoPlayer.include(VeedoPlayer._api_domain + '/template/account/js/flowplayer/flowplayer-3.2.12.min.js', function(){
			$.ajax({
				type: "POST",
				cache: false,
				url: VeedoPlayer._api_domain + '/api/GetDataCamera',
				data: {'camera_stream' : VeedoPlayer._options.camera_stream},
				dataType: 'json',
				success: function(msg){
					if(!msg.result.data){
						alert('error init player');
					}else if(msg.result.data.error){
						alert(msg.result.data.error);
					}else{
						VeedoPlayer._options.server = msg.result.data.src;
						VeedoPlayer.playerInit();
					}
				},
				error: function(e1, e2, e3){
					console.log(url, e1, e2, e3)
				}
			});
		});
	},

	play : function(){
		if(!this._player) return false;
		this._player.play();
	},

	pause : function(){
		if(!this._player) return false;
		this._player.pause();
	},

	playerInit : function(){
		var rtmp = this._parseSrc(this._options.server);

		var controls = null;
		if(this._options.controls){
			controls = {
				volume : true,
				mute : true,
				time : true,

				stop : false,
				play : false,
				fastForward : false,
				slowForward : false,
				//scrubber : false,
			}
		}

		$('#' + this._options.selector).css({
			'width' : this._options.width,
			'height': this._options.height
		});

		var player = $f(this._options.selector, this._api_domain + "/template/account/js/flowplayer/flowplayer-3.2.16.swf", {
			buffering : true,
			plugins: {
				rtmp: {
					url: this._api_domain + "/template/account/js/flowplayer/flowplayer.rtmp-3.2.12.swf",
					netConnectionUrl: rtmp[0]
					
				},
				controls: controls,				
			},
			clip: {
				autoPlay: this._options.autoPlay,
				autoBuffering: false,
				url: rtmp[1],
				scaling: 'fit',
				provider: 'rtmp',
			},
			onLoad : function(){
				VeedoPlayer._player = player				
			},
			onStart : function(){
				player.setVolume(0);
			},
		});
	},

	// include js file and use callback
	include : function(url, callback){
	    var head = document.getElementsByTagName('head')[0];
	    var script = document.createElement('script');
	    script.type = 'text/javascript';
	    script.src = url;

	    script.onreadystatechange = callback;
	    script.onload = callback;

	    head.appendChild(script);
	},

	// parse url
	_parseSrc : function(url){
		var play_file = url.split('mp4:');
		var server = play_file[0]
		var file = 'mp4:' + play_file[1]
		return [server, file]
	}
}