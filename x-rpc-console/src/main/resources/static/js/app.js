(function () {
    var currentView = 'apps';
    var refreshTimer = null;

    function init() {
        bindNav();
        loadView(currentView);
        startAutoRefresh();
    }

    function bindNav() {
        var items = document.querySelectorAll('.sidebar .nav-item');
        for (var i = 0; i < items.length; i++) {
            items[i].addEventListener('click', function () {
                var view = this.getAttribute('data-view');
                setActiveNav(view);
                loadView(view);
            });
        }
    }

    function setActiveNav(view) {
        currentView = view;
        var items = document.querySelectorAll('.sidebar .nav-item');
        for (var i = 0; i < items.length; i++) {
            items[i].classList.toggle('active', items[i].getAttribute('data-view') === view);
        }
    }

    function startAutoRefresh() {
        if (refreshTimer) clearInterval(refreshTimer);
        refreshTimer = setInterval(function () {
            loadView(currentView);
        }, 10000);
    }

    function loadView(view) {
        if (view === 'apps') {
            loadApps();
        } else if (view === 'services') {
            loadServices();
        } else if (view === 'instances') {
            loadInstances(currentView);
        }
    }

    function fetchJson(url, options) {
        return fetch(url, options || {}).then(function (res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        });
    }

    function loadApps() {
        var main = document.getElementById('main-content');
        fetchJson('/api/apps').then(function (apps) {
            if (!apps || apps.length === 0) {
                main.innerHTML = '<div class="empty">No applications registered</div>';
                return;
            }
            var html = '<div class="card"><div class="card-header"><h2>Applications</h2>'
                + '<span class="badge badge-blue">' + apps.length + ' apps</span></div>'
                + '<div class="card-body"><table><thead><tr><th>App Name</th><th>Instances</th><th>Actions</th></tr></thead><tbody>';
            for (var i = 0; i < apps.length; i++) {
                var app = apps[i];
                html += '<tr><td>' + escapeHtml(app.appName) + '</td>'
                    + '<td><span class="badge badge-blue">' + app.instanceCount + '</span></td>'
                    + '<td><button class="btn btn-primary" onclick="window.xrpc.viewInstances(\'' + escapeAttr(app.appName) + '\')">View Instances</button></td></tr>';
            }
            html += '</tbody></table></div></div>';
            main.innerHTML = html;
        }).catch(function (err) {
            main.innerHTML = '<div class="empty">Failed to load applications: ' + escapeHtml(err.message) + '</div>';
        });
    }

    function loadInstances(appName) {
        var main = document.getElementById('main-content');
        fetchJson('/api/apps/' + encodeURIComponent(appName) + '/instances').then(function (instances) {
            var html = '<div class="card"><div class="card-header"><h2>Instances of ' + escapeHtml(appName) + '</h2>'
                + '<button class="btn btn-primary" onclick="window.xrpc.backToApps()">Back to Apps</button></div>'
                + '<div class="card-body">';
            if (!instances || instances.length === 0) {
                html += '<div class="empty">No instances</div>';
            } else {
                html += '<table><thead><tr><th>ID</th><th>Address</th><th>Port</th><th>Protocol</th><th>Revision</th><th>Status</th><th>Registered</th><th>Actions</th></tr></thead><tbody>';
                for (var i = 0; i < instances.length; i++) {
                    var inst = instances[i];
                    var statusBadge = inst.enabled
                        ? '<span class="badge badge-green">Enabled</span>'
                        : '<span class="badge badge-red">Disabled</span>';
                    var actionBtn = inst.enabled
                        ? '<button class="btn btn-danger" onclick="window.xrpc.disableInstance(\'' + escapeAttr(appName) + '\',\'' + escapeAttr(inst.id) + '\')">Disable</button>'
                        : '<button class="btn btn-success" onclick="window.xrpc.enableInstance(\'' + escapeAttr(appName) + '\',\'' + escapeAttr(inst.id) + '\')">Enable</button>';
                    var regTime = inst.registrationTimestamp ? new Date(inst.registrationTimestamp).toLocaleString() : '-';
                    html += '<tr><td style="font-family:monospace;font-size:12px">' + escapeHtml(inst.id || '-') + '</td>'
                        + '<td>' + escapeHtml(inst.address || '-') + '</td>'
                        + '<td>' + (inst.port || '-') + '</td>'
                        + '<td>' + escapeHtml(inst.protocol || '-') + '</td>'
                        + '<td>' + escapeHtml(inst.revision || '-') + '</td>'
                        + '<td>' + statusBadge + '</td>'
                        + '<td>' + escapeHtml(regTime) + '</td>'
                        + '<td>' + actionBtn + '</td></tr>';
                }
                html += '</tbody></table>';
            }
            html += '</div></div>';

            var hasProps = instances && instances.some(function (inst) {
                return inst.props && Object.keys(inst.props).length > 0;
            });
            if (hasProps) {
                html += '<div class="card"><div class="card-header"><h2>Instance Properties</h2></div><div class="card-body">';
                for (var j = 0; j < instances.length; j++) {
                    var inst2 = instances[j];
                    if (inst2.props && Object.keys(inst2.props).length > 0) {
                        html += '<h3 style="font-size:14px;margin:12px 0 8px;color:#64748b">' + escapeHtml(inst2.address + ':' + inst2.port) + '</h3>';
                        html += '<table class="props-table"><thead><tr><th>Key</th><th>Value</th></tr></thead><tbody>';
                        var keys = Object.keys(inst2.props).sort();
                        for (var k = 0; k < keys.length; k++) {
                            html += '<tr><td>' + escapeHtml(keys[k]) + '</td><td>' + escapeHtml(String(inst2.props[keys[k]])) + '</td></tr>';
                        }
                        html += '</tbody></table>';
                    }
                }
                html += '</div></div>';
            }
            main.innerHTML = html;
        }).catch(function (err) {
            main.innerHTML = '<div class="empty">Failed to load instances: ' + escapeHtml(err.message) + '</div>';
        });
    }

    function loadServices() {
        var main = document.getElementById('main-content');
        fetchJson('/api/services').then(function (services) {
            if (!services || services.length === 0) {
                main.innerHTML = '<div class="empty">No services registered</div>';
                return;
            }
            var html = '<div class="card"><div class="card-header"><h2>Services</h2>'
                + '<span class="badge badge-blue">' + services.length + ' services</span></div>'
                + '<div class="card-body"><table><thead><tr><th>Interface</th><th>Providers</th><th>Actions</th></tr></thead><tbody>';
            for (var i = 0; i < services.length; i++) {
                var svc = services[i];
                html += '<tr><td style="font-family:monospace">' + escapeHtml(svc.interfaceName) + '</td>'
                    + '<td><span class="badge badge-green">' + svc.providerCount + '</span></td>'
                    + '<td><button class="btn btn-primary" onclick="window.xrpc.viewProviders(\'' + escapeAttr(svc.interfaceName) + '\')">View Providers</button></td></tr>';
            }
            html += '</tbody></table></div></div>';
            main.innerHTML = html;
        }).catch(function (err) {
            main.innerHTML = '<div class="empty">Failed to load services: ' + escapeHtml(err.message) + '</div>';
        });
    }

    function loadProviders(interfaceName) {
        var main = document.getElementById('main-content');
        fetchJson('/api/services/' + encodeURIComponent(interfaceName) + '/providers').then(function (providers) {
            var html = '<div class="card"><div class="card-header"><h2>Providers of ' + escapeHtml(interfaceName) + '</h2>'
                + '<button class="btn btn-primary" onclick="window.xrpc.backToServices()">Back to Services</button></div>'
                + '<div class="card-body">';
            if (!providers || providers.length === 0) {
                html += '<div class="empty">No providers</div>';
            } else {
                html += '<table><thead><tr><th>Address</th><th>Port</th><th>Protocol</th><th>Revision</th><th>Status</th></tr></thead><tbody>';
                for (var i = 0; i < providers.length; i++) {
                    var p = providers[i];
                    var statusBadge = p.enabled
                        ? '<span class="badge badge-green">Enabled</span>'
                        : '<span class="badge badge-red">Disabled</span>';
                    html += '<tr><td>' + escapeHtml(p.address || '-') + '</td>'
                        + '<td>' + (p.port || '-') + '</td>'
                        + '<td>' + escapeHtml(p.protocol || '-') + '</td>'
                        + '<td>' + escapeHtml(p.revision || '-') + '</td>'
                        + '<td>' + statusBadge + '</td></tr>';
                }
                html += '</tbody></table>';
            }
            html += '</div></div>';
            main.innerHTML = html;
        }).catch(function (err) {
            main.innerHTML = '<div class="empty">Failed to load providers: ' + escapeHtml(err.message) + '</div>';
        });
    }

    function escapeHtml(str) {
        if (str == null) return '';
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    function escapeAttr(str) {
        return escapeHtml(str).replace(/'/g, '&#39;');
    }

    window.xrpc = {
        viewInstances: function (appName) {
            currentView = appName;
            loadInstances(appName);
        },
        backToApps: function () {
            setActiveNav('apps');
            loadApps();
        },
        viewProviders: function (interfaceName) {
            loadProviders(interfaceName);
        },
        backToServices: function () {
            setActiveNav('services');
            loadServices();
        },
        disableInstance: function (appName, instanceId) {
            fetch('/api/apps/' + encodeURIComponent(appName) + '/instances/' + encodeURIComponent(instanceId) + '/disable', { method: 'PUT' })
                .then(function () { loadInstances(appName); });
        },
        enableInstance: function (appName, instanceId) {
            fetch('/api/apps/' + encodeURIComponent(appName) + '/instances/' + encodeURIComponent(instanceId) + '/enable', { method: 'PUT' })
                .then(function () { loadInstances(appName); });
        }
    };

    document.addEventListener('DOMContentLoaded', init);
})();
